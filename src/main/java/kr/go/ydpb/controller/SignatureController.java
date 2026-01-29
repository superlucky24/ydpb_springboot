package kr.go.ydpb.controller;

import kr.go.ydpb.domain.SignatureDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.util.Base64;

@Controller
@RequestMapping("/sub")
public class SignatureController {

    @Autowired
    private ResourceLoader resourceLoader;

    // A4 용지 기준 사이즈 (points)
    private static final float PDF_WIDTH = 595.27f;
    private static final float PDF_HEIGHT = 841.89f;

    @GetMapping("/signature")
    public String signaturePage() {
        return "sub/signature";
    }

    // 데이터 제출 및 PDF 생성 응답 / JS의 Ajax에서 /sub/submit으로 호출
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Resource> submitReport(@RequestBody SignatureDTO dto) {
        String outputFileName = "signed_report_" + System.currentTimeMillis() + ".pdf";
        // 서버 실행 루트 경로에 파일 생성
        File saveFile = new File(outputFileName);

        try {
            // 리소스 로드 경로 (알려주신 C:\SpringWorks... 경로 기준 클래스패스)
            Resource pdfTemplate = resourceLoader.getResource("classpath:static/images/sub/signature.pdf");
            Resource fontResource = resourceLoader.getResource("classpath:static/font/SpoqaHanSans-Regular.ttf");

            // 파일 로드 및 문서 생성
            try (InputStream pdfIs = pdfTemplate.getInputStream();
                 PDDocument document = PDDocument.load(pdfIs)) {

                PDPage page = document.getPage(0);

                // 폰트 로드 (별도 스트림으로 관리하여 안정성 확보)
                PDType0Font font;
                try (InputStream fontIs = fontResource.getInputStream()) {
                    font = PDType0Font.load(document, fontIs);
                }

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                    // 1. 상단 체크박스 (Spoqa 폰트 호환성을 위해 'V' 사용)
                    if (dto.getTopType() != null) {
                        float checkY = PDF_HEIGHT * (1 - 0.082f);
                        if (dto.getTopType().contains("c_top_1")) drawText(contentStream, font, 12, PDF_WIDTH * 0.089f, checkY, "V");
                        if (dto.getTopType().contains("c_top_2")) drawText(contentStream, font, 12, PDF_WIDTH * 0.248f, checkY, "V");
                        if (dto.getTopType().contains("c_top_3")) drawText(contentStream, font, 12, PDF_WIDTH * 0.341f, checkY, "V");
                    }

                    // 2. 신고인 정보 기입 (PDF 분석 기반 Y축 정밀 조정)
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.200f, PDF_HEIGHT * (1 - 0.211f) - 13, dto.getReporterName());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.680f, PDF_HEIGHT * (1 - 0.211f) - 13, dto.getReporterJumin());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.284f, PDF_HEIGHT * (1 - 0.248f) - 13, dto.getReporterRel());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.466f, PDF_HEIGHT * (1 - 0.248f) - 13, dto.getReporterTel());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.650f, PDF_HEIGHT * (1 - 0.254f) - 10, dto.getReporterPhone());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.200f, PDF_HEIGHT * (1 - 0.285f) - 13, dto.getReporterAddr());

                    // 3. 신고 사항 기입
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.240f, PDF_HEIGHT * (1 - 0.321f) - 18, dto.getReportContent());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.310f, PDF_HEIGHT * (1 - 0.362f) - 10, dto.getPrevMaster());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.624f, PDF_HEIGHT * (1 - 0.362f) - 10, dto.getCurrMaster());

                    // 4. 대상자 리스트 처리 (표 칸 높이 18.2f로 정밀 조정)
                    if (dto.getTargets() != null) {
                        float startY = PDF_HEIGHT * (1 - 0.442f) - 28;
                        for (int i = 0; i < dto.getTargets().size(); i++) {
                            SignatureDTO.TargetRowDTO row = dto.getTargets().get(i);
                            float currentY = startY - (i * 18.2f);
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.240f, currentY, row.getRel());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.333f, currentY, row.getName());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.436f, currentY, row.getJumin());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.578f, currentY, row.getPre());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.741f, currentY, row.getPost());
                        }
                    }

                    // 5. 하단 날짜 및 최종 성명 (하단부 위치 조정)
                    drawText(contentStream, font, 11, PDF_WIDTH * 0.690f, PDF_HEIGHT * (1 - 0.630f) - 18, dto.getSubmitYear());
                    drawText(contentStream, font, 11, PDF_WIDTH * 0.600f, PDF_HEIGHT * (1 - 0.667f) - 15, dto.getReporterName());

                    drawText(contentStream, font, 11, PDF_WIDTH * 0.700f, PDF_HEIGHT * (1 - 0.818f) - 18, dto.getSubmitYear());
                    drawText(contentStream, font, 11, PDF_WIDTH * 0.600f, PDF_HEIGHT * (1 - 0.842f) - 18, dto.getReporterName());

                    // 6. 서명 이미지 합성 (중앙 배치를 위한 좌표 조정)
                    drawImage(document, contentStream, dto.getSigPrev(), PDF_WIDTH * 0.4745f, PDF_HEIGHT * (1 - 0.34f) - 28, 65, 30);
                    drawImage(document, contentStream, dto.getSigCurr(), PDF_WIDTH * 0.7876f, PDF_HEIGHT * (1 - 0.34f) - 28, 65, 30);
                    drawImage(document, contentStream, dto.getSigReporter(), PDF_WIDTH * 0.753f, PDF_HEIGHT * (1 - 0.65f) - 25, 80, 40);
                    drawImage(document, contentStream, dto.getSigDelegate(), PDF_WIDTH * 0.763f, PDF_HEIGHT * (1 - 0.83f) - 25, 80, 35);
                }
                // 파일 저장
                document.save(saveFile);
            }

            // 결과 리턴
            Resource fileResource = new FileSystemResource(saveFile);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + outputFileName + "\"")
                    .body(fileResource);

        } catch (Exception e) {
            // 에러 발생 시 로그 상세 출력
            System.err.println("PDF 생성 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // 편의 메서드
    private void drawText(PDPageContentStream stream, PDType0Font font, int size, float x, float y, String text) throws Exception {
        if (text == null || text.trim().isEmpty()) return;
        stream.beginText();
        stream.setFont(font, size);
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.endText();
    }

    private void drawImage(PDDocument doc, PDPageContentStream stream, String base64Data, float x, float y, float w, float h) throws Exception {
        if (base64Data == null || !base64Data.contains(",")) return;
        try {
            String base64Image = base64Data.split(",")[1];
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, imageBytes, "img_" + System.currentTimeMillis());
            stream.drawImage(pdImage, x, y, w, h);
        } catch (Exception ignored) {}
    }
}