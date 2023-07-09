package com.demo.springbatch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.demo.springbatch.model.StudentCsv;
import com.demo.springbatch.model.StudentJson;

@Component
public class FirstItemWriter implements ItemWriter<StudentJson>{

	@Override
	public void write(List<? extends StudentJson> items) throws Exception {
		System.out.println("Inside Item Writer");
		items.stream().forEach(System.out::println);
		
	}

}
