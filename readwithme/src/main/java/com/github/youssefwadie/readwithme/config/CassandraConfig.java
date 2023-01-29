package com.github.youssefwadie.readwithme.config;

import com.github.youssefwadie.readwithme.connection.DataStaxAstraProperties;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class CassandraConfig {
    @Bean
    @Autowired
    public CqlSessionBuilderCustomizer cqlSessionBuilderCustomizer(final DataStaxAstraProperties astraProperties) {
        val bundlePath = astraProperties.getSecureConnectBundle().toPath();
        return sessionBuilder -> sessionBuilder.withCloudSecureConnectBundle(bundlePath);
    }
}
