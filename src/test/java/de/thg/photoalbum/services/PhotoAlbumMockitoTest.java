package de.thg.photoalbum.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PhotoAlbumMockitoTest {

    @Mock
    ImageMetadataReader imageMetadataReader;

    @InjectMocks
    private PhotoAlbumService underTest;

    @Test
    public void assertionWorked() {
        assertNotNull(underTest);
    }

}
