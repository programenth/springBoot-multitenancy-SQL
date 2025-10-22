package com.example.multitenancy;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableConfigurationProperties
public class DataSourceConfig {

    @Autowired
    private Environment env;

    private DataSource createHikari(String url, String user, String pass) {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setDriverClassName("org.postgresql.Driver");
        cfg.setMaximumPoolSize(10);
        return new HikariDataSource(cfg);
    }

    @Bean
    @Primary
    public DataSource routingDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        // read from properties (simplified here)
        DataSource ds1 = createHikari(env.getProperty("tenants.tenant1.jdbc-url"), env.getProperty("tenants.tenant1.username"),
          env.getProperty("tenants.tenant1.password"));
        DataSource ds2 = createHikari(env.getProperty("tenants.tenant2.jdbc-url"), env.getProperty("tenants.tenant2.username"),
          env.getProperty("tenants.tenant2.password"));

        targetDataSources.put("tenant1", ds1);
        targetDataSources.put("tenant2", ds2);

        RoutingDataSource routing = new RoutingDataSource();
        routing.setTargetDataSources(targetDataSources);
        routing.setDefaultTargetDataSource(ds1);
        routing.afterPropertiesSet();
        return routing;
    }

    // EntityManagerFactory using the routing DataSource
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource routingDataSource) {
        return builder.dataSource(routingDataSource)
          .packages("com.example.multitenancy") // your entities package
          .persistenceUnit("default")
          .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

