package de.thg.photoalbum.repositories;

import de.thg.photoalbum.model.Image;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(statements = {"insert into image (creation_date, filename, version) values (\'Date_1\', \'Image_1\', 0)",
        "insert into image (creation_date, filename, version) values (\'Date_2\', \'Image_2\', 0)"})
@SpringBootTest
@ActiveProfiles("tc")
class ImageRepositoryTest {

    @Inject
    ImageRepository imageRepository;

    @Test
    @Transactional
    void testFindImage() {
        Optional<Image> image1 = imageRepository.findByFilename("Image_2");
        assertThat(image1).isPresent();
        assertThat(image1.get().getCreationDate()).isEqualTo("Date_2");
    }

    @Test
    @Transactional
    void testFindBaNameStartingWith() {
        List<Image> images = imageRepository.findByFilenameStartingWith("Image");
        assertThat(images).hasSize(2);
    }

    @Test
    @Transactional
    void saveImage() {
        assertThat(imageRepository.findAll()).hasSize(2);
        Image image = new Image();
        image.setCreationDate("3");
        Image savedImage = imageRepository.saveAndFlush(image);
        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getCreationDate()).isEqualTo("3");
        assertThat(imageRepository.findAll()).hasSize(3);
        assertThat(imageRepository.findAll()).contains(savedImage);
        assertThat(savedImage).isEqualTo(image);
        assertThat(savedImage).isNotSameAs(image);
    }
}