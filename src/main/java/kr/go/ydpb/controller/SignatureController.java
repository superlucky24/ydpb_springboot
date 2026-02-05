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
import java.time.LocalDate;
import java.util.Base64;

@Controller
@RequestMapping("/sub")
public class SignatureController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final float PDF_WIDTH = 595.27f;
    private static final float PDF_HEIGHT = 841.89f;

    @GetMapping("/signature")
    public String signaturePage() {
        return "sub/signature";
    }

    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<Resource> submitReport(@RequestBody SignatureDTO dto) {
        String outputFileName = "signed_report_" + System.currentTimeMillis() + ".pdf";
        String basePath = System.getProperty("user.dir") + uploadDir;
        File saveFile = new File(basePath, outputFileName);

        try {
            Resource pdfTemplate = resourceLoader.getResource("classpath:static/images/sub/signature.pdf");
            Resource fontResource = resourceLoader.getResource("classpath:static/font/SpoqaHanSans-Regular.ttf");
            Resource stampResource = resourceLoader.getResource("classpath:static/images/sub/signature03.png");

            try (InputStream pdfIs = pdfTemplate.getInputStream();
                 PDDocument document = PDDocument.load(pdfIs)) {

                PDPage page = document.getPage(0);
                PDType0Font font;
                try (InputStream fontIs = fontResource.getInputStream()) {
                    font = PDType0Font.load(document, fontIs);
                }

                PDImageXObject staffStamp = null;
                try (InputStream stampIs = stampResource.getInputStream()) {
                    byte[] stampBytes = stampIs.readAllBytes();
                    staffStamp = PDImageXObject.createFromByteArray(document, stampBytes, "staff_stamp");
                } catch (Exception e) {
                    System.err.println("인감도장 로드 실패");
                }

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                    // 1. 상단 체크박스
                    if (dto.getTopType() != null) {
                        float checkY = PDF_HEIGHT * (1 - 0.094f);
                        if (dto.getTopType().contains("c_top_1")) drawText(contentStream, font, 12, PDF_WIDTH * 0.093f, checkY, "V");
                        if (dto.getTopType().contains("c_top_2")) drawText(contentStream, font, 12, PDF_WIDTH * 0.254f, checkY, "V");
                        if (dto.getTopType().contains("c_top_3")) drawText(contentStream, font, 12, PDF_WIDTH * 0.347f, checkY, "V");
                    }

                    // 2. 신고인 정보 기입
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

                    // 4. 대상자 리스트 처리
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

                    // 5. 중단 체크박스
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

                    // 7. 하단 체크박스
                    if (dto.getBtmType() != null) {
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

                    // 9. 서명 이미지 합성
                    drawImage(document, contentStream, dto.getSigPrev(), PDF_WIDTH * 0.512f - 30, PDF_HEIGHT * (1 - 0.365f) - 15, 60, 30);
                    drawImage(document, contentStream, dto.getSigCurr(), PDF_WIDTH * 0.827f - 30, PDF_HEIGHT * (1 - 0.365f) - 15, 60, 30);
                    drawImage(document, contentStream, dto.getSigReporter(), PDF_WIDTH * 0.827f - 40, PDF_HEIGHT * (1 - 0.670f) - 15, 80, 30);
                    drawImage(document, contentStream, dto.getSigDelegate(), PDF_WIDTH * 0.827f - 40, PDF_HEIGHT * (1 - 0.852f) - 13, 80, 26);

                    // 10. 행정 처리 영역 (이미지의 상단 회색 칸 위치로 대폭 상향 조정)
                    LocalDate now = LocalDate.now();
                    String receiptNo = now.getYear() + "-0001";

                    // 날짜 간격: 이미지의 "년 월 일" 텍스트 위치에 맞게 공백 대폭 추가
                    String yearStr = String.valueOf(now.getYear());
                    String monthStr = String.format("%d", now.getMonthValue());
                    String dayStr = String.format("%d", now.getDayOfMonth());
                    String todayFormatted = yearStr + "        " + monthStr + "        " + dayStr;

                    // adminY: PDF 상단 회색 칸(접수번호 칸)의 대략적인 위치 (상단에서 약 18% 지점)
                    float adminY = PDF_HEIGHT * (1 - 0.185f);

                    // A. 접수번호 / 신고일자
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.080f, adminY, receiptNo);
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.270f, adminY, todayFormatted);

                    // B. 주민등록표 처리 (박뽀삐)
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.485f, adminY, "박뽀삐");
                    if (staffStamp != null) contentStream.drawImage(staffStamp, PDF_WIDTH * 0.532f, adminY - 3, 15, 15);

                    // C. 관련업무 담당경유 (이뽀삐)
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.641f, adminY, "이뽀삐");
                    if (staffStamp != null) contentStream.drawImage(staffStamp, PDF_WIDTH * 0.688f, adminY - 3, 15, 15);

                    // D. 관계기관 통보 (최뽀삐)
                    drawText(contentStream, font, 9, PDF_WIDTH * 0.799f, adminY, "최뽀삐");
                    if (staffStamp != null) contentStream.drawImage(staffStamp, PDF_WIDTH * 0.846f, adminY - 3, 15, 15);
                }
                document.save(saveFile);
            }

            Resource fileResource = new FileSystemResource(saveFile);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + outputFileName + "\"")
                    .body(fileResource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

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