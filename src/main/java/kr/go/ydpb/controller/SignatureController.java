package kr.go.ydpb.controller;

import kr.go.ydpb.domain.SignatureDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir}")
    private String uploadDir;

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
        // 서버 업로드 경로에 파일 생성
        String basePath = System.getProperty("user.dir") + uploadDir; // 프로젝트 루트 + 업로드 경로
        File saveFile = new File(basePath, outputFileName);

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
                        float checkY = PDF_HEIGHT * (1 - 0.094f);
                        if (dto.getTopType().contains("c_top_1")) drawText(contentStream, font, 12, PDF_WIDTH * 0.093f, checkY, "V");
                        if (dto.getTopType().contains("c_top_2")) drawText(contentStream, font, 12, PDF_WIDTH * 0.254f, checkY, "V");
                        if (dto.getTopType().contains("c_top_3")) drawText(contentStream, font, 12, PDF_WIDTH * 0.347f, checkY, "V");
                    }

                    // 2. 신고인 정보 기입 (PDF 분석 기반 Y축 정밀 조정)
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.238f, PDF_HEIGHT * (1 - 0.221f), dto.getReporterName());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.685f, PDF_HEIGHT * (1 - 0.221f), dto.getReporterJumin());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.286f, PDF_HEIGHT * (1 - 0.258f), dto.getReporterRel());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.476f, PDF_HEIGHT * (1 - 0.258f), dto.getReporterTel());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.718f, PDF_HEIGHT * (1 - 0.258f), dto.getReporterPhone());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.202f, PDF_HEIGHT * (1 - 0.295f), dto.getReporterAddr());

                    // 3. 신고 사항 기입
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.250f, PDF_HEIGHT * (1 - 0.326f), dto.getReportContent());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.327f, PDF_HEIGHT * (1 - 0.368f), dto.getPrevMaster());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.629f, PDF_HEIGHT * (1 - 0.368f), dto.getCurrMaster());

                    // 4. 대상자 리스트 처리 (표 칸 높이 18.2f로 정밀 조정)
                    if (dto.getTargets() != null) {
                        float startY = PDF_HEIGHT * (1 - 0.446f);
                        for (int i = 0; i < dto.getTargets().size(); i++) {
                            SignatureDTO.TargetRowDTO row = dto.getTargets().get(i);
                            float currentY = startY - (i * 27.3f);
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.240f, currentY, row.getRel());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.333f, currentY, row.getName());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.434f, currentY, row.getJumin());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.578f, currentY, row.getPre());
                            drawText(contentStream, font, 9, PDF_WIDTH * 0.742f, currentY, row.getPost());
                        }
                    }

                    // 5. 중단 체크박스 (Spoqa 폰트 호환성을 위해 'V' 사용)
                    if (dto.getMidType() != null) {
                        float checkY = PDF_HEIGHT * (1 - 0.621f);
                        if (dto.getMidType().contains("c_mid_1")) drawText(contentStream, font, 10, PDF_WIDTH * 0.283f, checkY, "V");
                        if (dto.getMidType().contains("c_mid_2")) drawText(contentStream, font, 10, PDF_WIDTH * 0.412f, checkY, "V");
                        if (dto.getMidType().contains("c_mid_3")) drawText(contentStream, font, 10, PDF_WIDTH * 0.487f, checkY, "V");
                    }

                    // 6. 신고 날짜 및 신고인
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.690f, PDF_HEIGHT * (1 - 0.640f), dto.getSubmitYear());
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.778f, PDF_HEIGHT * (1 - 0.640f), dto.getSubmitMonth());
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.837f, PDF_HEIGHT * (1 - 0.640f), dto.getSubmitDay());
                    drawText(contentStream, font, 12, PDF_WIDTH * 0.633f, PDF_HEIGHT * (1 - 0.675f), dto.getReporterName());

                    // 7. 하단 체크박스 (Spoqa 폰트 호환성을 위해 'V' 사용)
                    if (dto.getMidType() != null) {
                        float checkY = PDF_HEIGHT * (1 - 0.797f);
                        if (dto.getBtmType().contains("c_btm_1")) drawText(contentStream, font, 10, PDF_WIDTH * 0.669f, checkY, "V");
                        if (dto.getBtmType().contains("c_btm_2")) drawText(contentStream, font, 10, PDF_WIDTH * 0.786f, checkY, "V");
                        if (dto.getBtmType().contains("c_btm_3")) drawText(contentStream, font, 10, PDF_WIDTH * 0.855f, checkY, "V");
                    }

                    // 8. 위임 날짜 및 위임한 사람
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.690f, PDF_HEIGHT * (1 - 0.8276f), dto.getSubmitYear());
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.778f, PDF_HEIGHT * (1 - 0.8276f), dto.getSubmitMonth());
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.837f, PDF_HEIGHT * (1 - 0.8276f), dto.getSubmitDay());
                    drawText(contentStream, font, 10, PDF_WIDTH * 0.633f, PDF_HEIGHT * (1 - 0.853f), dto.getDelegateName());

                    // 9. 서명 이미지 합성 (중앙 배치를 위한 좌표 조정 및 CSS 박스 비율 반영)
                    // [수정] 배경 PDF 칸 이탈 방지를 위해 이미지 크기를 과감히 축소하고 Y 오프셋을 더 내림
                    // 전/현 세대주: (기존 64x36 -> 48x28 축소 / Y오프셋 -22로 하향 조정)
                    drawImage(document, contentStream, dto.getSigPrev(), PDF_WIDTH * 0.512f - 24, PDF_HEIGHT * (1 - 0.365f) - 22, 48, 28);
                    drawImage(document, contentStream, dto.getSigCurr(), PDF_WIDTH * 0.827f - 24, PDF_HEIGHT * (1 - 0.365f) - 22, 48, 28);

                    // 신고인: (기존 80x30 -> 60x24 축소 / Y오프셋 -24로 하향 조정)
                    drawImage(document, contentStream, dto.getSigReporter(), PDF_WIDTH * 0.827f - 30, PDF_HEIGHT * (1 - 0.670f) - 24, 60, 24);

                    // 위임인: (기존 72x24 -> 56x20 축소 / Y오프셋 -20으로 하향 조정)
                    drawImage(document, contentStream, dto.getSigDelegate(), PDF_WIDTH * 0.827f - 28, PDF_HEIGHT * (1 - 0.852f) - 20, 56, 20);
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