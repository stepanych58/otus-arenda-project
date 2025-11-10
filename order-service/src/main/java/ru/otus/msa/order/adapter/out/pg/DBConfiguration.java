package ru.otus.msa.order.adapter.out.pg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.time.Instant;
import java.util.Optional;

@Configuration
public class DBConfiguration {

    @Bean
    public AuditorAware<Instant> auditorAware() {
        return () -> Optional.of(Instant.now()); // always UTC
    }
}
