package org.nacosdemo.tlmallorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TlmallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TlmallOrderApplication.class, args);
	}

}
