package de.thg.photoalbum.repositories;

import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.testcontainers.AbstractContainerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ImageRepositoryIT extends AbstractContainerBaseTest {

    @Inject
    ImageRepository imageRepository;

    @BeforeEach
    void setup() {
        imageRepository.deleteAll();
        Image image1 = new Image("Image_1", LocalDateTime.of(2023, 10, 13, 14, 30));
        Image image2 = new Image("Image_2", LocalDateTime.of(2023, 10, 13, 14, 31));
        imageRepository.saveAll(List.of(image1, image2));
    }

    @Test
    void testFindImage() {
        List<Image> image1 = imageRepository.findByFilename("Image_2");
        assertThat(image1).isNotEmpty();
        assertThat(image1.get(0).getCreationDate()).isEqualTo(LocalDateTime.of(2023, 10, 13, 14, 31));
    }

    @Test
    void testFindByNameStartingWith() {
        List<Image> images = imageRepository.findByFilenameStartingWith("Image");
        assertThat(images).hasSize(2);
    }

    @Test
    void saveImage() {
        assertThat(imageRepository.findAll()).hasSize(2);
        Image image = new Image("", LocalDateTime.of(2023, 10, 13, 14, 32));
        Image savedImage = imageRepository.save(image);
        assertThat(savedImage.getId()).isNotNull();
        assertThat(savedImage.getCreationDate()).isEqualTo(LocalDateTime.of(2023, 10, 13, 14, 32));
        assertThat(imageRepository.findAll()).hasSize(3);
        assertThat(imageRepository.findAll()).contains(savedImage);
        assertThat(savedImage).isSameAs(image);
    }

    @Test
    void updateImage() {
        Image image = new Image("Image_3", LocalDateTime.of(2023, 10, 13, 14, 32));
        assertThat(image.getId()).isNull();
        imageRepository.save(image);
        String imageId = image.getId();
        assertThat(imageId).isNotNull();
        assertThat(imageRepository.findAll()).hasSize(3);
        image.setCreationDate(LocalDateTime.of(2023, 10, 13, 14, 32,1));
        imageRepository.save(image);
        assertThat(imageRepository.findAll()).hasSize(3);
        assertThat(image.getId()).isEqualTo(imageId);
    }
}