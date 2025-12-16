package de.thg.photoalbum.integration;

import de.thg.photoalbum.testcontainers.AbstractContainerBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class PhotoAlbumRestClientIT extends AbstractContainerBaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void testGetAll() {
        restTestClient.get()
                .uri("http://localhost:%d/photoalbum/photos".formatted(port))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}
