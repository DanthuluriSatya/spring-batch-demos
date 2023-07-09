package com.demo.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.demo.springbatch.model.StudentJson;
import com.demo.springbatch.model.StudentXml;
@Component
/*
public class FirstItemProcessor implements ItemProcessor<Integer, Long>//input for item reader is integer and output is long
{
//take output fromiteam reader and pass it as input here. 
	@Override
	public Long process(Integer item) throws Exception {
		System.out.println("Inside Item processor");
		return Long.valueOf(item+20);//convert int to long 
	}

}*/
public class FirstItemProcessor implements ItemProcessor<StudentXml, StudentJson>//input for item reader is integer and output is long
{
	@Override
	public StudentJson process(StudentXml item) throws Exception {
		System.out.println("Inside Item processor");
		StudentJson studentJson= new StudentJson();
		studentJson.setId(item.getId());
		studentJson.setFirstName(item.getFirstName());
		studentJson.setLasstName(item.getLastName());
		studentJson.setFirstName(item.getFirstName());
		return studentJson;//convert int to long 
	}

}