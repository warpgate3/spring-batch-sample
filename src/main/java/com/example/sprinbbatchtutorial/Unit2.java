package com.example.sprinbbatchtutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;

/** reader (read to csv) -> writer (write DB) */
@Configuration
@Slf4j
public class Unit2 { public static final String JOB_NAME = "unit2_job";
  public static final String STEP_NAME = "unit2_step";
  public static final String READER_NAME = "unit2_reader";
  public static final String WRITER_NAME = "unit2_writer";
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;
  public Unit2(
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
        .<SampleDto, SampleDto>chunk(10)
        .reader(unitReader())
        .writer(unitWriter())
        .build();
  }

  @Bean(name = READER_NAME)
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

  @Bean(name = WRITER_NAME)
  public ItemWriter<SampleDto> unitWriter() {
    return new JdbcBatchItemWriterBuilder<SampleDto>()
        .dataSource(dataSource)
        .sql(
            "insert into sample (srch_yn, "
                + "org_nm, "
                + "cycle, "
                + "stat_nm, "
                + "stat_cd, "
                + "p_stat_cd, "
                + "base_dt, "
                + "batch_log_id) "
                + "values ("
                + ":srchYn, "
                + ":orgNm, "
                + ":cycle, "
                + ":statNm, "
                + ":statCd, "
                + ":pStatCd, "
                + ":baseDt, "
                + ":batchLogId)")
        .beanMapped()
        .build();
  }
}
