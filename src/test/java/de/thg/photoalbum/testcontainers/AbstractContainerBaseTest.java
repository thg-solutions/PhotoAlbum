package de.thg.photoalbum.testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer postgres;

    static {
        postgres = new PostgreSQLContainer("postgres:15.3-alpine3.18")
                .withUsername("tom")
                .withPassword("tom")
                .withDatabaseName("photoalbum");
        postgres.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.data.mongodb.host", mongodb::getHost);
    }
}
