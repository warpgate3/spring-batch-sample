drop table sample;
create table sample (
                        srch_yn varchar2(10),
                        org_nm varchar2(100),
                        cycle varchar2(200),
                        stat_nm varchar2(200),
                        stat_cd varchar2(12),
                        p_stat_cd varchar2(12),
                        base_dt varchar2(8),
                        batch_log_id varchar2(50)
);

drop table sample2;
create table sample2 (
                        stat_nm varchar2(200),
                        stat_cd varchar2(12),
                        std_dt varchar2(8)
);

DROP TABLE SAMPLE3;
create table sample3 (
                        std_dt varchar2(8),
                        srch_yn varchar2(10),
                        org_nm varchar2(100),
                        cycle varchar2(200),
                        stat_nm varchar2(200),
                        stat_cd varchar2(12),
                        p_stat_cd varchar2(12),
                        base_dt varchar2(8),
                        batch_log_id varchar2(50)
);