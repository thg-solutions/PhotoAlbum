package de.thg.photoalbum;

import de.thg.photoalbum.testcontainers.AbstractContainerBaseTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PhotoAlbumApplication.class)
@AutoConfigureMockMvc
public class PhotoAlbumAppIT extends AbstractContainerBaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost/";
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
    }

    @Test
    void testGetAllSpringMvc() throws Exception {
        mvc.perform(get("/photoalbum/photos").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().equals("[]"));
    }
}
