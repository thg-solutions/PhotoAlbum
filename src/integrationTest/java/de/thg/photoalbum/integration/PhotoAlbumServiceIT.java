package de.thg.photoalbum.integration;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.repositories.ImageRepository;
import de.thg.photoalbum.services.PhotoAlbumService;
import de.thg.photoalbum.testcontainers.AbstractContainerBaseTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PhotoAlbumServiceIT extends AbstractContainerBaseTest {

    private static Path sourcepath1;
    private static Path sourcepath2;
    private static Path targetpath;

    private AlbumParams params;

    @Inject
    private PhotoAlbumService underTest;

    @Inject
    private ImageRepository imageRepository;

    @Value("${photoalbum.prefix:jpg}")
    private String PREFIX;

    @BeforeAll
    static void beforeAll() throws IOException {
        targetpath = Files.createTempDirectory("target_");
        sourcepath1 = Files.createTempDirectory("source1_");
        sourcepath2 = Files.createTempDirectory("source2_");
        File destFile1 = new File(sourcepath1.toFile(), "PHOTO0021.JPG");
        File destFile2 = new File(sourcepath2.toFile(), "PHOTO0083.JPG");
        Files.copy(PhotoAlbumServiceIT.class.getResourceAsStream("/testdata/PHOTO0021.JPG"), destFile1.toPath());
        Files.copy(PhotoAlbumServiceIT.class.getResourceAsStream("/testdata/PHOTO0083.JPG"), destFile2.toPath());
    }

    @AfterAll
    static void afterAll() {
        File target = targetpath.toFile();
        FileUtils.deleteQuietly(target);
        File source1 = sourcepath1.toFile();
        FileUtils.deleteQuietly(source1);
        File source2 = sourcepath2.toFile();
        FileUtils.deleteQuietly(source2);
    }

    @BeforeEach
    public void setUp() {
        params = new AlbumParams();
        params.getSources().add(sourcepath1.toString());
        params.getSources().add(sourcepath2.toString());
        params.setTarget(targetpath.toString());
        params.setDebug(false);
    }

    @Test
    public void testCreateOrUpdatePhotoAlbum() {
        imageRepository.deleteAll();
        assertThat(imageRepository.findAll()).as("table image is not empty").isEmpty();
        assertThat(params.isDebug()).isFalse();
        assertThat(new File(params.getTarget())).isDirectory();
//		assertThat(new File(params.getTarget()).listFiles()).isEmpty();
        for (String source : params.getSources()) {
            assertThat(new File(source)).isDirectory();
            assertThat(new File(source).listFiles()).isNotEmpty();
        }
        List<Image> imageList = underTest.createOrUpdatePhotoAlbum(params);
        assertThat(imageList).hasSize(2);
        for (Image thisImage : imageList) {
            assertThat(thisImage.getTempFile()).isNotNull();
        }
        imageRepository.saveAll(imageList);
        for (Image imageFromDb : imageRepository.findAll()) {
            assertThat(imageFromDb.getCreationDate().equals(imageList.get(0).getCreationDate()) ||
                    imageFromDb.getCreationDate().equals(imageList.get(1).getCreationDate())).as("error in DB").isTrue();
            assertThat(imageFromDb.getTempFile()).isNull();
            assertThat(imageFromDb.getFilename()).as("wrong filename").startsWith(PREFIX);
        }
    }

    @Test
    @Disabled
    void renameImageFiles() throws IOException {
        String sourcedir = "/home/tom/Bilder/Test";
        assertThat(new File(sourcedir).listFiles()).isNotEmpty();
        List<Image> images = underTest.renameImageFiles(sourcedir);
        assertThat(images).isNotEmpty();
        assertThat(images.stream().map(Image::getFilename).filter(s -> !s.matches("\\d{8}_\\d{6}.jpg")).toList()).isEmpty();
        Collections.sort(images);
        for (Image image : images) {
            System.out.println(image.getFilename());
        }

//		System.out.println();
//		sourcedir = "/home/tom/Bilder/PhotoAlbum";
//		assertThat(new File(sourcedir).listFiles()).isNotEmpty();
//		assertThat(Arrays.stream(new File(sourcedir).listFiles()).map(File::getName).filter(s -> s.matches("\\d{8}_\\d{6}.jpg")).toList()).isEmpty();
//		images = underTest.renameImageFiles(sourcedir);
//		assertThat(images).isNotEmpty();
//		assertThat(images.stream().map(Image::getFilename).filter(s -> !s.matches("\\d{8}_\\d{6}.jpg")).toList()).isEmpty();
//		Collections.sort(images);
//		for (Image image : images) {
//			System.out.println(image.getFilename());
//		}
    }

}
