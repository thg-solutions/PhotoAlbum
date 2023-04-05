package de.thg.photoalbum.repositories;

import de.thg.photoalbum.model.Image;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ImageRepositoryTest {

    @Inject
    ImageRepository imageRepository;

    @Test
    @Disabled
    void findAll() {
        List<Image> images = imageRepository.findAll();
        assertThat(images).isNotEmpty();
        assertThat(images.stream().map(Image::getFilename).map(s -> s.startsWith("PHOTO"))
                .distinct().collect(Collectors.toList())).containsExactly(Boolean.TRUE);
    }

    @Test
    @Transactional
    void saveImage() {
        Image image = new Image();
        image.setCreationDate("1");
        Image savedImage = imageRepository.saveAndFlush(image);
        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getCreationDate()).isEqualTo("1");
    }
}