package com.example.sprinbbatchtutorial;

import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class Sample2Dao {
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	public Sample2Dao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public int rollbackData(String stdDt) {
		SqlParameterSource params = new MapSqlParameterSource(
				Map.of(
						"stdDt", stdDt
				)
		);
		return namedParameterJdbcTemplate.update("delete from sample2 where std_dt = :stdDt", params);
	}
}
