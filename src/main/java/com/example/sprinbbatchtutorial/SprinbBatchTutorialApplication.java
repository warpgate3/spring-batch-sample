package com.example.sprinbbatchtutorial;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
public class SprinbBatchTutorialApplication implements CommandLineRunner {
  @Autowired private JobLauncher jobLauncher;
  @Autowired private ApplicationContext context;
  @Autowired private JobExplorer jobExplorer;

  public static void main(String[] args) {
    SpringApplication.run(SprinbBatchTutorialApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    Job jobBean = this.context.getBean("tunit5_job", Job.class);

    Properties properties = new Properties();

    JobParameters params = new JobParametersBuilder(properties).toJobParameters();

    JobParameters jobParameters =
        new JobParametersBuilder(params, this.jobExplorer)
            .getNextJobParameters(jobBean)
            .toJobParameters();

    final AtomicInteger id = new AtomicInteger();
//    CompletableFuture.supplyAsync(
//        () -> {
//          JobExecution run = null;
          try {
            JobExecution run = this.jobLauncher.run(jobBean, jobParameters);
            System.out.println("ID:"  + run.getId());
          } catch (JobExecutionAlreadyRunningException
              | JobRestartException
              | JobInstanceAlreadyCompleteException
              | JobParametersInvalidException e) {
            e.printStackTrace();
          }
//          id.setjj
//          return null;
//        });

    System.out.println("5초후 잡이 종료됩니다.--->" + id.get());
    TimeUnit.SECONDS.sleep(5);

    SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
    simpleJobOperator.stop(id.get());
    System.out.println("잡이 종료됐습니다..");
  }
}
