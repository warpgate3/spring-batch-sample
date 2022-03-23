package com.example.sprinbbatchtutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class Unit7 {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    public static final String JOB_NAME = "UNIT7_JOB";

    public Unit7(
        final JobBuilderFactory jobBuilderFactory,
        final StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean(name = JOB_NAME)
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
            .incrementer(new UniqueRunIdIncrementer())
            .start(step())
            .build();
    }

    @Bean(name = JOB_NAME + "_STEP")
    @JobScope
    public Step step() {
        return stepBuilderFactory.get(JOB_NAME + "_STEP")
            .<String, String>chunk(1)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    @Bean(name = JOB_NAME + "_READER")
    @StepScope
    public ItemReader<String> reader() {
        AtomicInteger counter = new AtomicInteger(0);
        List<String> names = List.of("a", "b", "c", "d",
            "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
            "v", "w", "x", "y", "z");

        return () -> {
            TimeUnit.SECONDS.sleep(3);
            if (counter.get() < names.size()) {
                return names.get(counter.getAndIncrement());
            }
            return null;
        };
    }

    @Bean(name = JOB_NAME + "_PROCESSOR")
    @StepScope
    public ItemProcessor<String, String> processor() {
        return item -> {
            return "[" + item + "]";
        };
    }

    @Bean(name = JOB_NAME + "_WRITER")
    @StepScope
    public ItemWriter<String> writer() {
        return items -> {
            System.out.println(items);
        };
    }
}
