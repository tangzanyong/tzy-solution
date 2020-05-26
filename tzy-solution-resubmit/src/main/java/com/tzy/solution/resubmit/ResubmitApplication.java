package com.tzy.solution.resubmit;

import com.tzy.solution.resubmit.config2.CacheKeyGenerator;
import com.tzy.solution.resubmit.config2.LockKeyGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author tangzanyong
 * @description @TODO
 * @date 2020/5/22
 **/
@SpringBootApplication
public class ResubmitApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResubmitApplication.class, args);
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }
}
