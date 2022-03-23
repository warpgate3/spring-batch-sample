package com.example.sprinbbatchtutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Unit5AroundListener implements org.springframework.batch.core.ChunkListener {
  private final Sample2Dao sample2Dao;

  public Unit5AroundListener(Sample2Dao sample2Dao) {
    this.sample2Dao = sample2Dao;
  }

  @Override
  public void beforeChunk(ChunkContext context) {
    log.info("before chunk. [context:{}]", context);
  }

  @Override
  public void afterChunk(ChunkContext context) {
    log.info("after chunk. [context:{}]", context);
  }

  @Override
  public void afterChunkError(ChunkContext context) {
    final StepContext stepContext = context.getStepContext();
    final String stdDt = (String) stepContext.getJobParameters().get("std_dt");

    //clear committed data
//    sample2Dao.rollbackData(stdDt);
  }
}
