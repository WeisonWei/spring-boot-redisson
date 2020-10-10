package com.weison.sbr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.repository.query.QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

/**
 * @author WeisonWei
 * @date 2020/10/10
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EntityScan(value = {"com.weison.sbr.entity"})
@EnableJpaRepositories(basePackages = "com.weison.sbr.repository", queryLookupStrategy = CREATE_IF_NOT_FOUND)
public class SpringBootRedissonApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedissonApplication.class, args);
    }

}
