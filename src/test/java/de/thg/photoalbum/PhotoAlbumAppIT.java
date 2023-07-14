package de.thg.photoalbum;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = PhotoAlbumApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("tc")
public class PhotoAlbumAppIT {

    @LocalServerPort
    private int ports;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost/photoalbum";
        RestAssured.port = ports;
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
