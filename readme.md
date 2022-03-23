# Spring Batch Example
Spring Batch Examples

## Run Job
yml 파일에 설정을 명시하거나 spring boot run 실행시 변수로 입력
- YML 파일
```yml
  batch:
    job:
      enabled: true
      names: unit3_job #실행시킬 Job 이름 

```
- 파라미터 주입
```shell
--spring.batch.job.names=unit3_job
--job.name=tunit4_job std_dt=20220407
```


## Introduce Jobs
Spring Batch 를 이용한 Job 을 처리하는 방식은 크게 Reader-Processor-Writer 를 이용한 방식과 Tasklet 을 이용한 2가지 방식으로 제공한다.

### Chunk (Reader-Processor-Writer) 방식 
* Unit1 (CSV -> CSV)
 
    sample.csv 파일을 읽어서 srch_yn 값이 N 인 record 만 result.csv 파일에 저장한다.
  

* Unit2 (CSV -> DB)

    sample.csv 파일을 읽어서 모든 record 를 DB 에 저장


* Unit3 (CSV -> DB, parallel)

  sample.csv 파일을 병렬로 읽어서 모든 record를 DB 에 저장 


* Unit4 (DB -> DB)

    DB to DB, sample table 에서 sample2 table로 저장 


* Unit5 (CSV -> DB, Chunk listener)

    Spring Batch 는 Chunk size 를 설정할 수 있고 Chunk Size 별로
Commit 을 한다. 이경우 3번째 Chunk 에서 오류가 발생할 경우 1, 2번째 chunk 데이터는 삭제가 안 되는데
chunk listener 로 commit 된 데이터를 삭제한다. 


* Unit6 (CSV -> DB, insert after error point)
  
    Spring Batch는 오류가 발생할 경우 재시작할 때 저장된 데이터 이후로 저장 처리를 한다. 예를 들어 800건 중 501번째 오류가 발생하면
  오류가 발생한 원인을 정정하고 재 실행시에 501번째부터 저장처리를 한다. 
  ![img.png](img.png)
  한 번 실행 후 위 오류를 발생시킨 501 번째 라인의 , 삭제한 뒤 재실행하면 데이터가 800건만 들어 간 것을 확인할 수 있다.

* Unit7 

  Chunk Style 일 배치로  Writer, Processor, Reader 기본 클래스들을 구현해서 Chunk 배치의 간단한 flow 를 확인한다. 
### Tasklet 을 이용한 처리

* Tunit1 

  입력날짜를 출력하는 기본적인 Tasklet 구조


* Tunit2 (CSV -> DB)
  
  opencsv 라이브러리를 통해 CSV 파일을 읽어서 DB 에 저장한다. csv READ TASKLET 과 DB WRITE TASKLET 을 각 각 2개의 STEP 으로 구분해서
  등록했다. 이 경우 문제점은 CSV STEP 정상적으로 전체를 읽은 후에 다음 DB 저장 sTEP 에서 오류가 발생했을 경우 전역변수로 사용한 리스트에 문제가 생기기 때문에
  2번째 실행시 첫번째 STEP 이 Completed 되었기 때문에 실행하지 않는다.


* Tunit3 (csv -> DB)

  tasklet 은 별도의 트랜잭션 선언 없이 한 개의 트랜잭션으로 동작한다. 이 부분을 확인을 위한 테스트 샘플이다. tuni3.csv 마지막 라인데
  데이터 사이즈를 컬럼 사이즈보다 크게 해서 오류를 발생해고 모든 데이터가 원복 되는지 확인해보자.


* Tunit4 (simple)

  tasklet 실행시 spring batch 에서 제공하는 메타 테이블의 상태 종료 값을 확인한다.
  메타 테이블인 batch_step_execution, batch_job_execution status필드를 확인한다.
  - UNKNOWN : 배치 실행중 강제 종료 시킬 경우 발생한다. 이 경우 Endtime 도 확인할 수 없다.
  - STARTED: 배치가 실행된 상태이다.
  - COMPLETED: 배치가 종료된 상태이다.
  - EXECUTING: 배치가 실행중이다.
  - FAILED: 배치가 오류로 인해 중단됐다.