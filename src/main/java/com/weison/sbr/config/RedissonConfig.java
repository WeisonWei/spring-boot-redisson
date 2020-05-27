package com.weison.sbr.config;


import com.weison.sbr.util.DistributedLockUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RedissonConfig {

    @Value("${spring.redis.url:}")
    private String address;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.database:0}")
    private String database;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        SingleServerConfig singleServerConfig = config.useSingleServer();
        if (StringUtils.isNotBlank(address)) {
            singleServerConfig.setAddress(address);
        }

        if (StringUtils.isNotBlank(password)) {
            singleServerConfig.setPassword(password);
        }

        if (StringUtils.isNotBlank(database)) {
            singleServerConfig.setDatabase(Integer.valueOf(database));

        }
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

    @Bean
    public DistributedLock distributedLocker(RedissonClient redissonClient) {
        DistributedLock locker = new RedissonDistributedLock();
        ((RedissonDistributedLock) locker).setRedissonClient(redissonClient);
        DistributedLockUtil.setLocker(locker);
        return locker;
    }
}
