package com.demo.springbatch.all;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.demo.springbatch.listener.FirstStepListener;
import com.demo.springbatch.listener.firstJobListener;
import com.demo.springbatch.model.StudentCsv;
import com.demo.springbatch.model.StudentJson;
import com.demo.springbatch.model.StudentXml;
import com.demo.springbatch.processor.FirstItemProcessor;
import com.demo.springbatch.reader.FirstItemReader;
import com.demo.springbatch.service.SecondTasklet;
import com.demo.springbatch.writer.FirstItemWriter;

@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private SecondTasklet secondTasklet;

	@Autowired
	private firstJobListener firstJobListener;

	@Autowired
	private FirstStepListener firstStepListener;

	@Autowired
	private FirstItemReader firstItemReader;

	@Autowired
	private FirstItemProcessor firstItemProcessor;

	@Autowired
	private FirstItemWriter firstItemWriter;
	
	 @Value("${outputFile}") 
	private FileSystemResource fileSystemResource;

	@Bean("firstJob")
	public Job firstJob() {
		return jobBuilderFactory.get("First Job").incrementer(new RunIdIncrementer()).start(firstStep())
				.next(secondStep()).listener(firstJobListener).build();
	}

	@Bean
	public Step firstStep() {
		return stepBuilderFactory.get("First Step").tasklet(firstTask()).listener(firstStepListener).build();
	}

	private Tasklet firstTask() {
		return new Tasklet() {

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("****** this is first Tasklet step ****");
				// accessing first step from here
				System.out.println("sec= " + chunkContext.getStepContext().getStepExecutionContext());
				return RepeatStatus.FINISHED;
			}
		};
	}

	private Step secondStep() {
		return stepBuilderFactory.get("First Step").tasklet(secondTasklet) // providing instance of interface
				.build();
	}

	@Bean("secondJob")
	public Job secondJob() {
		return jobBuilderFactory.get("Second Job")
				.incrementer(new RunIdIncrementer())
				.start(firstChunkStep())
				.next(secondStep()).build();
	}

	private Step firstChunkStep() {
		System.out.println("firstItemProcessor " + firstItemProcessor);
		return stepBuilderFactory.get("Second Step").<StudentXml, StudentXml>chunk(3) // providing chunk size to read
																						// this size of chunk
				// .reader(flatFileItemReader())
				.reader(staxEventItemReader())
				// .reader(jsonItemReader())
				//.processor(firstItemProcessor)
				//.writer(firstItemWriter)
				//.writer(flatFileItemWriter())
				.writer(jsonFileItemWriter())
				.build();
	}

	public FlatFileItemReader<StudentCsv> flatFileItemReader() {
		// @Value("#{jobParameters['inputFile']") FileSystemResource
		// fileSystemResource){

		FlatFileItemReader<StudentCsv> flatFileItemReader = new FlatFileItemReader<>();

		flatFileItemReader
				.setResource(new FileSystemResource(new File("C:\\Code\\spring-batch\\InputFiles\\students.csv")));

		flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames("ID", "First Name", "Last Name", "Email");
					}

				});

				setFieldSetMapper((new BeanWrapperFieldSetMapper<StudentCsv>() {
					{
						setTargetType(StudentCsv.class);
					}
				}));

			}
		});

		/*
		 * DefaultLineMapper<StudentCsv> defaultLineMapper = new
		 * DefaultLineMapper<StudentCsv>();
		 * 
		 * DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		 * delimitedLineTokenizer.setNames("ID", "First Name", "Last Name", "Email");
		 * 
		 * defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		 * 
		 * BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper = new
		 * BeanWrapperFieldSetMapper<StudentCsv>();
		 * fieldSetMapper.setTargetType(StudentCsv.class);
		 * 
		 * defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		 * 
		 * flatFileItemReader.setLineMapper(defaultLineMapper);
		 * 
		 */
		flatFileItemReader.setLinesToSkip(1);// skip heading
		return flatFileItemReader;
	}

	@StepScope
	@Bean
	public JsonItemReader<StudentJson> jsonItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<StudentJson>();

		jsonItemReader.setResource(fileSystemResource);
		jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader<>(StudentJson.class));

		// jsonItemReader.setMaxItemCount(8);
		// jsonItemReader.setCurrentItemCount(2);

		return jsonItemReader;
	}

	public StaxEventItemReader<StudentXml> staxEventItemReader() {
		System.out.println("**Inside staxEventItemReader**");

		StaxEventItemReader<StudentXml> staxEventItemReader = new StaxEventItemReader<>();

		staxEventItemReader
				.setResource(new FileSystemResource(new File("C:\\Code\\spring-batch\\InputFiles\\students.xml")));
		staxEventItemReader.setFragmentRootElementName("student");
		staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
			{
				setClassesToBeBound(StudentXml.class);
			}
		});

		return staxEventItemReader;
	}
	
	public FlatFileItemWriter<StudentXml> flatFileItemWriter(){
		FlatFileItemWriter<StudentXml> flatFileItemWriter=new FlatFileItemWriter<StudentXml>();
	
		flatFileItemWriter.setResource(new FileSystemResource(new File("C:\\Code\\spring-batch\\OutputFiles\\students.csv")));
		flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {

			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("ID,First Name,Last Name,Email");
				
			}
			
		});
		flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<StudentXml>(){
		{
			//setDelimiter("|");by default delimiter is ,
		setFieldExtractor(new BeanWrapperFieldExtractor<StudentXml>() {
			{
				setNames(new String[] {"id","firstName","lastName","email"});
			}
		});	
	}
		});
	  flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
		
		@Override
		public void writeFooter(Writer writer) throws IOException {
			System.out.println("writting file");
			writer.write("Created@"+new Date());
			
		}
	});
		return flatFileItemWriter;
		
	}
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentXml> jsonFileItemWriter()
	{
		JsonFileItemWriter<StudentXml> jsonFileItemWriter = 
				new JsonFileItemWriter<>(fileSystemResource, 
						new JacksonJsonObjectMarshaller<StudentXml>());
		
		
		return jsonFileItemWriter;
	}
}
