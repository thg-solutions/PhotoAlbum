package de.thg.photoalbum.repositories;

import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.testcontainers.AbstractContainerBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ImageRepositoryTest extends AbstractContainerBaseTest {

    @Inject
    ImageRepository imageRepository;

    @Test
    @Transactional
    void saveImage() {
        assertThat(imageRepository.findAll()).isEmpty();
        Image image = new Image();
        image.setCreationDate("1");
        Image savedImage = imageRepository.saveAndFlush(image);
        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getCreationDate()).isEqualTo("1");
        assertThat(imageRepository.findAll()).hasSize(1);
        assertThat(imageRepository.findAll()).contains(savedImage);
        assertThat(savedImage).isEqualTo(image);
        assertThat(savedImage).isNotSameAs(image);
    }
}