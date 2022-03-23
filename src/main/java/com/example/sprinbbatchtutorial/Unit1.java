package com.example.sprinbbatchtutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

/** reader (read to csv) -> processo(srch_yn y 인 값만) -> writer (write csv file) */
@Configuration
@Slf4j
public class Unit1 {
  public static final String JOB_NAME = "unit1_job";
  public static final String STEP_NAME = "unit1_step";
  public static final String READER_NAME = "unit1Reader";
  public static final String WRITER_NAME = "unit1Writer";
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public Unit1(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean(name=JOB_NAME)
  public Job unitJob() {
    return jobBuilderFactory
        .get(JOB_NAME)
        .start(unitStep(null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean(name=STEP_NAME)
  @JobScope
  public Step unitStep(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return stepBuilderFactory
        .get(STEP_NAME)
        .<SampleDto, SampleDto>chunk(10)
        .reader(unitReader())
        .processor(unit1Processor())
        .writer(unitWriter())
        .build();
  }

  @Bean(name=READER_NAME)
  public ItemReader<SampleDto> unitReader() {
    return new FlatFileItemReaderBuilder<SampleDto>()
        .name(READER_NAME)
        .encoding("utf-8")
        .resource(new FileSystemResource("data/sample.csv"))
        .strict(false)
        .delimited()
        .delimiter(",")
        .names("srchYn", "orgNm", "cycle", "statNm", "statCd", "pStatCd", "baseDt", "batchLogId")
        .linesToSkip(1)
        .fieldSetMapper(
            new BeanWrapperFieldSetMapper<>() {
              {
                setTargetType(SampleDto.class);
              }
            })
        .build();
  }

  @Bean
  public ItemProcessor<SampleDto, SampleDto> unit1Processor() {
    return item -> {
      if ("N".equals(item.getSrchYn())) {
        return item;
      }
      return null;
    };
  }

  @Bean(name=WRITER_NAME)
  public ItemWriter<SampleDto> unitWriter() {
    return new FlatFileItemWriterBuilder<SampleDto>()
        .name(WRITER_NAME)
        .append(false)
        .encoding("utf-8")
        .resource(new FileSystemResource("data/result.csv"))
        .delimited()
        .delimiter(",")
        .names("srchYn", "orgNm", "cycle", "statNm", "statCd", "pStatCd", "baseDt", "batchLogId")
        .build();
  }
}
