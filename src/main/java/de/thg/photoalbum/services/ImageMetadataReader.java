package de.thg.photoalbum.services;

import de.thg.photoalbum.model.Image;

import java.io.File;

public interface ImageMetadataReader {

    Image readImageMetadata(File file);
}
