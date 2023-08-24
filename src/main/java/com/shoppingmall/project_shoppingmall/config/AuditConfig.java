package com.shoppingmall.project_shoppingmall.config;

import org.springframework.context.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.*;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

}
