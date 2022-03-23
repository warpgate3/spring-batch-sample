package com.example.sprinbbatchtutorial;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

/**
*   reader (table sample)  -> writer (table sample2)
*
* */
@Configuration
@Slf4j
public class Unit5 {
  public static final String JOB_NAME = "unit5_job";
  public static final String STEP_NAME = "unit5_step";
  public static final String READER_NAME = "unit5_reader";
  public static final String PROCESSOR_NAME = "unit5_processor";
  public static final String WRITER_NAME = "unit5_writer";
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;
  private final Unit5AroundListener unit5AroundListener;
  public Unit5(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory,
      DataSource dataSource,
      Unit5AroundListener unit5AroundListener) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.dataSource = dataSource;
    this.unit5AroundListener = unit5AroundListener;
  }

  @Bean(name = JOB_NAME)
  public Job unitJob() {
    return jobBuilderFactory
        .get(JOB_NAME)
        .start(unitStep(null))
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean(name = STEP_NAME)
  @JobScope
  public Step unitStep(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return stepBuilderFactory
        .get(STEP_NAME)
        .<SampleDto, Sample2Dto>chunk(5)
        .reader(unitReader())
        .processor(unitProcessor(stdDt))
        .writer(unitWriter())
        .listener(unit5AroundListener)
        .build();
  }

  @Bean(name = READER_NAME)
  @StepScope
  public FlatFileItemReader<SampleDto> unitReader() {
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

  @Bean(name = PROCESSOR_NAME)
  @StepScope
  public ItemProcessor<SampleDto, Sample2Dto> unitProcessor(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return item -> {
        Sample2Dto sample2Dto = new Sample2Dto();
        sample2Dto.setStatCd(item.getStatCd());
        sample2Dto.setStatNm(item.getStatNm());
        sample2Dto.setStdDt(stdDt);
        return sample2Dto;
    };
  }

  @Bean(name = WRITER_NAME)
  @StepScope
  public ItemWriter<Sample2Dto> unitWriter() {
      log.info("writer: current thread --> {}", Thread.currentThread().getName());
    return new JdbcBatchItemWriterBuilder<Sample2Dto>()
        .dataSource(dataSource)
        .sql(
            "insert into sample2 ( "
                + "stat_nm, "
                + "std_dt, "
                + "stat_cd) "
                + "values ("
                + ":statNm, :stdDt, :statCd)"
        )
        .beanMapped()
        .build();
  }
}
