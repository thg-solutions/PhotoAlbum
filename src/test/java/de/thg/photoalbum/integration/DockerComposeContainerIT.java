package de.thg.photoalbum.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Testcontainers
public class DockerComposeContainerIT {

    @Container
    public static ComposeContainer composeContainer =
            new ComposeContainer(new File("src/main/resources/compose.yaml"))
                    .withExposedService("photoalbum-1", 8080);

    private String host;
    private int port;

    @BeforeEach
    void Setup() {
        host = composeContainer.getServiceHost("photoalbum-1", 8080);
        port = composeContainer.getServicePort("photoalbum-1", 8080);
    }

    @Test
    void dockerNetworkStarted() {
        RestAssured.baseURI = host;
        RestAssured.port = port;
//        given()
//                .when().get("http://localhost:" + port + "/photoalbum/photos")
//                .then().assertThat().statusCode(HttpStatus.OK.value());
    }
}
