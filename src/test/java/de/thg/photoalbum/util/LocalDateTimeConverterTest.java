package de.thg.photoalbum.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocalDateTimeConverterTest {

    @Autowired
    private LocalDateTimeConverter undertest;

    @Test
    void testToLocalDateTime() {
        String dateString = "2023:10:12 11:15:00";
        LocalDateTime localDateTime = undertest.toLocalDateTime(dateString);
        assertThat(localDateTime).isEqualTo(LocalDateTime.of(2023, 10, 12, 11, 15, 0));
    }

    @Test
    void testLdtToFilename() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 12, 11, 2, 0);
        String filename = undertest.toFilename(localDateTime);
        assertThat(filename).isEqualTo("20231012_110200.jpg");
    }

    @Test
    void testStringToFilename() {
        String filename = undertest.toFilename("2023:10:12 11:15:00");
        assertThat(filename).isEqualTo("20231012_111500.jpg");
    }
}