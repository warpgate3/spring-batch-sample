package com.example.sprinbbatchtutorial;

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

@Configuration
@Slf4j
public class Tunit1 {
  public static final String JOB_NAME = "tunit1_job";
  public static final String STEP_NAME = "tunit1_step";
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public Tunit1(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean(name = JOB_NAME)
  public Job unitJob() {
    return jobBuilderFactory.get(JOB_NAME).start(unitStep(null)).build();
  }

  @Bean(name = STEP_NAME)
  @JobScope
  public Step unitStep(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return stepBuilderFactory.get(STEP_NAME).tasklet(taskletImpl(stdDt)).build();
  }

  @Bean(name = JOB_NAME + "-TASKLET")
  @StepScope
  public Tasklet taskletImpl(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return (stepContribution, chunkContext) -> {
      log.info("std => [{}]", stdDt);
      return RepeatStatus.FINISHED;
    };
  }
}
