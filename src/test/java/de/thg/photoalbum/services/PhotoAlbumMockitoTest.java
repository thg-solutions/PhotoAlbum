package de.thg.photoalbum.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class PhotoAlbumMockitoTest {

    @Mock
    ImageMetadataReader imageMetadataReader;

    @InjectMocks
    private PhotoAlbumService underTest;

    @Test
    public void assertionWorked() {
        assertThat(underTest).isNotNull();
    }

}
