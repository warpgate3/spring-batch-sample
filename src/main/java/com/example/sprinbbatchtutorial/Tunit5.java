package com.example.sprinbbatchtutorial;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
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
public class Tunit5 {
  public static final String JOB_NAME = "tunit5_job";
  public static final String STEP_NAME = "tunit5_step";
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public Tunit5(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean(name = JOB_NAME)
  public Job unitJob() {
    return jobBuilderFactory.get(JOB_NAME)
    .start(readStep(null))
        .incrementer(parameters -> new JobParametersBuilder(parameters)
            .addDate(
                "timestamp",
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
            .toJobParameters())
    .build();
  }

  @Bean(name = STEP_NAME)
  @JobScope
  public Step readStep(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return stepBuilderFactory.get(STEP_NAME).tasklet(readTasklet(stdDt)).build();
  }

  @Bean(name = JOB_NAME + "_Tsk")
  @StepScope
  public Tasklet readTasklet(@Value("#{jobParameters['std_dt']}") String stdDt) {
    return (stepContribution, chunkContext) -> {
      IntStream.range(1, 5)
          .forEach(
              i -> {
                try {
                  TimeUnit.SECONDS.sleep(2);
                  System.out.println("No:" + i);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              });

      return RepeatStatus.FINISHED;
    };
  }

  public static void main(String[] args) {
    for (int i = 0; i < 5; i++) {
      System.out.println("No:" + i);
    }
  }
}
