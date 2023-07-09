package com.demo.springbatch.all;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmpController {
	
	
	@GetMapping(value ="/demo")
	public long gettime() {
		return System.currentTimeMillis();
	}
	

}
