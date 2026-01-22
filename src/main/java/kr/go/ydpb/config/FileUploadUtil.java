package kr.go.ydpb.config;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUploadUtil {

    public String saveFile(MultipartFile file, String uploadDir) throws IOException {
        String original = file.getOriginalFilename();
        String saved = UUID.randomUUID() + "_" + original;

        File dest = new File(uploadDir + File.separator + saved);
        file.transferTo(dest);

        return saved;
    }

    public void deleteFile(String path) {
        File file = new File(path);
        if(file.exists()) file.delete();
    }
}
