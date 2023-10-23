package de.thg.photoalbum.testcontainers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.Assert;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public abstract class AbstractContainerBaseTest {

    private static final Logger LOGGER = LogManager.getLogger(AbstractContainerBaseTest.class);

    // keine JUnit5-Annotationen! Sonst würde der Container nach jeder Test-Klasse abgeräumt,
    // aber für die nächste Klasse, die diese abstrakte Klasse erweitert, nicht neu gebaut.
    private static final MongoDBContainer mongodb = new MongoDBContainer(DockerImageName.parse("mongodb/mongodb-community-server:7.0.1-ubi8").asCompatibleSubstituteFor("mongo"));

    static {
        mongodb.start();
        LOGGER.info("MongoDB URL: " + mongodb.getReplicaSetUrl());
        Assert.isTrue(mongodb.isHostAccessible(), "MongoDB host not accessable");
        Assert.isTrue(mongodb.isHealthy(), "MongoDB is unhealthy");
        Assert.isTrue(mongodb.isRunning(), "MongoDB not running");
    }

    @DynamicPropertySource
    // ist eigens dafür da, Properties dynamisch erst dann zu setzen, wenn sie bekannt sind,
    // also nach dem Start eines Containers. Es ist unklar, wann oder wie oft das genau passiert,
    // aber die empfohlene Annotation @BeforeAll wird auch einmal pro Test-Klasse aufgerufen, also
    // öfter als nötig. System.setProperty(...) im static-Block wäre eine Alternative.
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.port", mongodb::getFirstMappedPort);
        registry.add("spring.data.mongodb.host", mongodb::getHost);
    }
}
