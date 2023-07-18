/*
package de.thg.photoalbum.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static io.restassured.RestAssured.given;

@Testcontainers
public class DockerComposeContainerIT {

    @Container
    public static DockerComposeContainer composeContainer =
            new DockerComposeContainer(new File("src/main/resources/compose.yaml"))
                    .withExposedService("photoalbum_1", 8080);

    private String host;
    private int port;

    @BeforeEach
    void Setup() {
        host = composeContainer.getServiceHost("photoalbum_1", 8080);
        port = composeContainer.getServicePort("photoalbum_1", 8080);
    }

    @Test
    @Disabled("Noch gibt es den angekündigten ComposeContainer nicht. Warten auf nächstes Release.")
    void dockerNetworkStarted() {
        RestAssured.baseURI = host;
        RestAssured.port = port;
        given()
                .when().get("http://localhost:" + port + "/photoalbum/photos")
                .then().assertThat().statusCode(HttpStatus.OK.value());
    }
}
*/
