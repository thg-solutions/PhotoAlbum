package de.thg.photoalbum.testcontainers;

import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("tc")
public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer postgres;

    static {
        postgres = new PostgreSQLContainer("postgres:15.3-alpine3.18")
            .withDatabaseName("photoalbum");
        postgres.start();
    }

}
