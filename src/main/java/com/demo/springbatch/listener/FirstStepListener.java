package com.demo.springbatch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class FirstStepListener implements StepExecutionListener{

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Before step"+stepExecution.getStepName());
		System.out.println("Job Execution Context"+stepExecution.getJobExecution().getExecutionContext());
		System.out.println("step excution context  "+stepExecution.getJobExecution().getExecutionContext());
		
		stepExecution.getExecutionContext().put("sec","sec value");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("After step"+stepExecution.getStepName());
		System.out.println("Job Execution Context"+stepExecution.getJobExecution().getExecutionContext());
		System.out.println("step excution context  "+stepExecution.getJobExecution().getExecutionContext());
		return null;
	}

}
