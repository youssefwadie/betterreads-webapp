package com.github.youssefwadie.readwithmedataloader.config;

import com.github.youssefwadie.readwithmedataloader.connection.DataStaxAstraProperties;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {
    @Bean
    @Autowired
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(final DataStaxAstraProperties astraProperties) {
        val bundle = astraProperties.getSecureConnectBundle().toPath();
        return sessionBuilder -> sessionBuilder.withCloudSecureConnectBundle(bundle);
    }
}
