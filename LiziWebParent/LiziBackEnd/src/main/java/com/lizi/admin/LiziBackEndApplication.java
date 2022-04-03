package com.lizi.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.lizi.common.entity", "com.lizi.admin.user"})
public class LiziBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiziBackEndApplication.class, args);
	}

}
