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
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

/** reader (table sample)  -> writer (table sample2) */
@Configuration
@Slf4j
public class Unit4 {
  public static final String JOB_NAME = "unit4_job";
  public static final String STEP_NAME = "unit4_step";
  public static final String READER_NAME = "unit4_reader";
  public static final String PROCESSOR_NAME = "unit4_processor";
  public static final String WRITER_NAME = "unit4_writer";
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;

  public Unit4(
          JobBuilderFactory jobBuilderFactory,
          StepBuilderFactory stepBuilderFactory,
          DataSource dataSource) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.dataSource = dataSource;
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
        .<SampleDto, Sample2Dto>chunk(10)
        .reader(unitReader())
        .writer(unitWriter())
        .build();
  }

  @Bean(name = READER_NAME)
  public ItemReader<SampleDto> unitReader() {
    return new JdbcCursorItemReaderBuilder<SampleDto>()
            .name(READER_NAME)
            .fetchSize(10)
            .dataSource(dataSource)
            .rowMapper(new BeanPropertyRowMapper<>(SampleDto.class))
            .sql("select stat_nm, stat_cd from sample")
            .build();
  }

  @Bean(name = PROCESSOR_NAME)
  public ItemProcessor<SampleDto, Sample2Dto> unitProcessor() {
    return item -> {
      if ("N".equals(item.getSrchYn())) {
        Sample2Dto sample2Dto = new Sample2Dto();
        sample2Dto.setStatCd(item.getStatCd());
        sample2Dto.setStatNm(item.getStatNm());
        return sample2Dto;
      }
      return null;
    };
  }

  @Bean(name = WRITER_NAME)
  public ItemWriter<Sample2Dto> unitWriter() {
      log.info("writer: current thread --> {}", Thread.currentThread().getName());
    return new JdbcBatchItemWriterBuilder<Sample2Dto>()
        .dataSource(dataSource)
        .sql(
            "insert into sample2 ( "
                + "stat_nm, "
                + "stat_cd) "
                + "values ("
                + ":statNm, :statCd)"
        )
        .beanMapped()
        .build();
  }
}
