package com.example.sprinbbatchtutorial;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfigurer extends DefaultBatchConfigurer {
	private static final String TABLE_PREFIX = "batch_";

	private final DataSource dataSource;

	private final PlatformTransactionManager platformTransactionManager;

	public BatchConfigurer(DataSource dataSource,
			PlatformTransactionManager platformTransactionManager) {
		this.dataSource = dataSource;
		this.platformTransactionManager = platformTransactionManager;
	}

	@Override
	protected JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setTransactionManager(platformTransactionManager);
		factory.setDataSource(dataSource);
		factory.setTablePrefix(TABLE_PREFIX);
		factory.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
		return factory.getObject();
	}

	@Override
	protected JobExplorer createJobExplorer() throws Exception {
		JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTablePrefix(TABLE_PREFIX);
		factory.afterPropertiesSet();
		return factory.getObject();
	}
}
