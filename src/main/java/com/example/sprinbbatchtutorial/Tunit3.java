package com.example.sprinbbatchtutorial;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.io.FileReader;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class Tunit3 {
  public static final String JOB_NAME = "tunit3_job";
  public static final String STEP_NAME = "tunit3_step";
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private List<Sample3Dto> sourceList;
  public Tunit3(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  @Bean(name = JOB_NAME)
  public Job unitJob() {
    return
            jobBuilderFactory.get(JOB_NAME)
                    .start(readStep(null))
                    .next(writeStep(null))
                    .build();
  }

  @Bean(name = STEP_NAME+"_read")
  @JobScope
  public Step readStep(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return stepBuilderFactory.get(STEP_NAME+"_read")
            .tasklet(readTasklet(stdDt))
            .build();
  }

  @Bean(name = STEP_NAME+"_write")
  @JobScope
  public Step writeStep(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return stepBuilderFactory.get(STEP_NAME+"_write")
            .tasklet(writeTasklet(stdDt))
            .build();
  }

  @Bean(name = JOB_NAME + "-R-TASKLET")
  @StepScope
  public Tasklet readTasklet(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return (stepContribution, chunkContext) -> {
      log.info("std => [{}]", stdDt);
      sourceList = new CsvToBeanBuilder(
              new FileReader("D:\\my-pjt\\spring-batch-sample\\data\\tunit3.csv"))
              .withType(Sample3Dto.class)
              .build()
              .parse();

      //rollback 안 된다.
      namedParameterJdbcTemplate.update("insert into tran_test (val) values(:val)", Map.of("val", "1"));
      return RepeatStatus.FINISHED;
    };
  }

  @Bean(name = JOB_NAME + "-W-TASKLET")
  @StepScope
  public Tasklet writeTasklet(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return (stepContribution, chunkContext) -> {

      //ROLLBACK 된다.
      namedParameterJdbcTemplate.update("insert into tran_test (val) values(:val)", Map.of("val", "2"));

      SqlParameterSource[] mapSqlParameterSources = this.sourceList.stream().map(sample3Dto -> new MapSqlParameterSource()
              .addValue("baseDt", sample3Dto.getBaseDt())
              .addValue("cycle", sample3Dto.getCycle())
              .addValue("batchLogId", sample3Dto.getBatchLogId())
              .addValue("orgNm", sample3Dto.getOrgNm())
              .addValue("statCd", sample3Dto.getStatCd())
              .addValue("srchYn", sample3Dto.getSrchYn())
              .addValue("statNm", sample3Dto.getStatNm())
              .addValue("pStatCd", sample3Dto.getPStatCd())
              .addValue("stdDt", stdDt))
              .toArray(SqlParameterSource[]::new);

      namedParameterJdbcTemplate.batchUpdate("insert into sample3 (std_dt, srch_yn, org_nm, cycle, stat_nm, stat_cd, p_stat_cd, base_dt, batch_log_id)" +
                      " values(:stdDt, :srchYn, :orgNm, :cycle, :statNm, :statCd, :pStatCd, :baseDt, :batchLogId)",
              mapSqlParameterSources);
      return RepeatStatus.FINISHED;
    };
  }
}
