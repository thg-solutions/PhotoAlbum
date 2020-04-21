package de.thg.photoalbum.services;

import de.thg.photoalbum.model.Image;

import java.io.File;
import java.io.IOException;

public interface ImageMetadataReader {

    Image readImageMetadata(File file) throws IOException;
}
