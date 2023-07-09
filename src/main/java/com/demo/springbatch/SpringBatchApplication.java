package com.demo.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableBatchProcessing
@ComponentScan({"com.demo.springbatch.all","com.demo.springbatch.service"
	,"com.demo.springbatch.listener","com.demo.springbatch.reader",
	"com.demo.springbatch.writer",
	"com.demo.springbatch.processor"})
public class SpringBatchApplication extends Spring {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchApplication.class, args);
	}

}
