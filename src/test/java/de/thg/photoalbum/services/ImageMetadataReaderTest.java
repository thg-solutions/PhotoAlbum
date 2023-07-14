package de.thg.photoalbum.services;

import de.thg.photoalbum.model.Image;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class ImageMetadataReaderTest {

    @Inject
    @Qualifier("apache-imaging")
    ImageMetadataReader apacheImageReader;

    @Inject
    @Qualifier("metadata-extractor")
    ImageMetadataReader metadataExtractorReader;

    @Test
    public void compareImageReaders() throws IOException {
        Tika tika = new Tika();
        URL url = ImageMetadataReaderTest.class.getResource("/testdata");

        File imageDir = new File(url.getFile());
        assertThat(imageDir).exists();
        assertThat(imageDir).isDirectory();

        File[] imageFileList = imageDir.listFiles();
        assertThat(Arrays.asList(imageFileList)).isNotEmpty();

        for (File imageFile : imageFileList) {
            assertThat(tika.detect(imageFile)).isEqualTo("image/jpeg");
            Image apacheImage = apacheImageReader.readImageMetadata(new FileInputStream(imageFile), imageFile.getName());
            assertThat(apacheImage).isNotNull();
            Image metadataExtracorImage = metadataExtractorReader.readImageMetadata(new FileInputStream(imageFile), imageFile.getName());
            assertThat(metadataExtracorImage).isNotNull();
            assertThat(apacheImage).usingRecursiveComparison().isEqualTo(metadataExtracorImage);
        }
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        @Qualifier("apache-imaging")
        public ApacheImageReader apacheImageReader() {
            return new ApacheImageReader();
        }

        @Bean
        @Qualifier("metadata-extractor")
        public MetadataExtractorReader metadataExtractorReader() {
            return new MetadataExtractorReader();
        }
    }
}