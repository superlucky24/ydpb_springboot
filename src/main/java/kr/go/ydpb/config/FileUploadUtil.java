package kr.go.ydpb.config;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUploadUtil {
/* 파일 업로드 관련 config */
    public String saveFile(MultipartFile file, String uploadDir) throws IOException {
        /* 저장할 파일 이름 가져옴 */
        String original = file.getOriginalFilename();
        /* 저장 시의 이름 생성 : 랜덤 UUID_이름*/
        String saved = UUID.randomUUID() + "_" + original;

        /* uploadDir(properties에 적은 경로) 아래에 저장될 파일 경로 생성  ex) uploadDir + / + image.jpg */
        File dest = new File(uploadDir + File.separator + saved);
        /* 실제 서버 폴더에 저장 작업 실행 */
        file.transferTo(dest);
        /* 파일 명 반환 */
        return saved;
    }

    public void deleteFile(String path) {
        /* path 경로로 file(object) 생성 */
        File file = new File(path);
        /* file 값이 존재시 삭제 */
        if(file.exists()) file.delete();
    }
}
