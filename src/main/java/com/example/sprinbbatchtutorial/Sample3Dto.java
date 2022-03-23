package com.example.sprinbbatchtutorial;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Sample3Dto {
  @CsvBindByPosition(position = 0)
  private String srchYn;

  @CsvBindByPosition(position = 1)
  private String orgNm;

  @CsvBindByPosition(position = 2)
  private String cycle;

  @CsvBindByPosition(position = 3)
  private String statNm;

  @CsvBindByPosition(position = 4)
  private String statCd;

  @CsvBindByPosition(position = 5)
  private String pStatCd;

  @CsvBindByPosition(position = 6)
  private String baseDt;

  @CsvBindByPosition(position = 7)
  private String batchLogId;
}
