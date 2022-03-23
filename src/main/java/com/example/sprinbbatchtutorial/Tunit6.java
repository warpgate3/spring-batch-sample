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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class Tunit6 {
    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    public static final String JOB_NAME = "TUNIT6_JOB";

    public Tunit6(
        final JobBuilderFactory jobBuilderFactory,
        final StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean(name = JOB_NAME)
    public Job job() {
        return jobBuilderFactory
            .get(JOB_NAME)
            .incrementer(new UniqueRunIdIncrementer())
            .start(initMeta())
            .build();
    }

    @Bean(name = JOB_NAME + "_STEP")
    @JobScope
    public Step initMeta() {
        return this.stepBuilderFactory
            .get(JOB_NAME + "_STEP")
            .tasklet(metaInitTasklet())
            .build();
    }

    @Bean(name = JOB_NAME + "-TASKLET")
    @StepScope
    public Tasklet metaInitTasklet() {
        return (stepContribution, chunkContext) -> {
            Long jobExecutionId = chunkContext.getStepContext().getStepExecution()
                .getJobExecutionId();
            log.info("jobExecutionId: {}", jobExecutionId);
            for (int i = 0; i < 50; i++) {
                System.out.println("Job is running");
                TimeUnit.SECONDS.sleep(3);
            }
            return RepeatStatus.FINISHED;
        };
    }
}
