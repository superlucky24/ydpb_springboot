package kr.go.ydpb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /* 시큐리티로 대체
    private final AdminCheckInterceptor adminCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**");
    }
    */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 프로젝트 루트 기준 upload 폴더 절대경로 생성
        String projectRoot = Paths.get("").toAbsolutePath().toString();
        String uploadPath = "file:" + projectRoot + "/upload/";

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}
