package com.example.sprinbbatchtutorial;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

public class TimestampIncreamenter implements JobParametersIncrementer {

  @Override
  public JobParameters getNext(JobParameters parameters) {
    return new JobParametersBuilder(parameters)
        .addDate(
            "timestamp",
            Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
        .toJobParameters();
  }
}
