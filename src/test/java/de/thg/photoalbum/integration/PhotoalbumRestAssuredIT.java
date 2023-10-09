package de.thg.photoalbum.integration;

import de.thg.photoalbum.testcontainers.AbstractContainerBaseTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhotoalbumRestAssuredIT extends AbstractContainerBaseTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testGetAll() {
        given().contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/photoalbum/photos")
                .then().assertThat().statusCode(HttpStatus.OK.value());
    }



}
