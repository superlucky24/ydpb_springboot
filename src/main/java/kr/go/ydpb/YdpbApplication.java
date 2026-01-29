package kr.go.ydpb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;

@SpringBootApplication
@MapperScan("kr.go.ydpb.mapper")
public class YdpbApplication {

	public static void main(String[] args) {
		SpringApplication.run(YdpbApplication.class, args);
	}

}
