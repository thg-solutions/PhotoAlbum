package de.thg.photoalbum.repositories;

import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.testcontainers.AbstractContainerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ImageRepositoryIT extends AbstractContainerBaseTest {

    @Inject
    ImageRepository imageRepository;

    @BeforeEach
    void setup() {
        imageRepository.deleteAll();
        Image image1 = new Image("Image_1", "Date_1");
        Image image2 = new Image("Image_2", "Date_2");
        imageRepository.saveAll(List.of(image1, image2));
    }

    @Test
    void testFindImage() {
        Optional<Image> image1 = imageRepository.findByFilename("Image_2");
        assertThat(image1).isPresent();
        assertThat(image1.get().getCreationDate()).isEqualTo("Date_2");
    }

    @Test
    void testFindByNameStartingWith() {
        List<Image> images = imageRepository.findByFilenameStartingWith("Image");
        assertThat(images).hasSize(2);
    }

    @Test
    void saveImage() {
        assertThat(imageRepository.findAll()).hasSize(2);
        Image image = new Image("", "3");
        Image savedImage = imageRepository.save(image);
        assertThat(savedImage.getId()).isNotNull();
        assertThat(savedImage.getCreationDate()).isEqualTo("3");
        assertThat(imageRepository.findAll()).hasSize(3);
        assertThat(imageRepository.findAll()).contains(savedImage);
        assertThat(savedImage).isSameAs(image);
    }

    @Test
    void updateImage() {
        Image image = new Image("Image_3", "Date_3");
        assertThat(image.getId()).isNull();
        imageRepository.save(image);
        String imageId = image.getId();
        assertThat(imageId).isNotNull();
        assertThat(imageRepository.findAll()).hasSize(3);
        image.setCreationDate("Date_3.1");
        imageRepository.save(image);
        assertThat(imageRepository.findAll()).hasSize(3);
        assertThat(image.getId()).isEqualTo(imageId);
    }
}