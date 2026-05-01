package com.mdau.proelitecars.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RailwayDatabaseConfig
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();
        String dbUrl = env.getProperty("DATABASE_URL", "");

        if (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://")) {
            String normalized = dbUrl
                .replace("postgresql://", "postgres://")
                .replace("postgres://", "");

            String userInfo = normalized.split("@")[0];
            String username = userInfo.split(":")[0];
            String password = userInfo.split(":")[1];
            String hostAndDb = normalized.split("@")[1];
            String host = hostAndDb.split("/")[0];
            String dbName = hostAndDb.split("/")[1];
            String finalUrl = "jdbc:postgresql://" + host + "/" + dbName;

            Map<String, Object> props = new HashMap<>();
            props.put("spring.datasource.url", finalUrl);
            props.put("spring.datasource.username", username);
            props.put("spring.datasource.password", password);

            env.getPropertySources().addFirst(
                new MapPropertySource("railwayDatasource", props));

            log.info("✅ Converted DB URL to: {}", finalUrl);
        }
    }
}