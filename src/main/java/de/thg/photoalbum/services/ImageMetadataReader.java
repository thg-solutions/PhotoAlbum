package de.thg.photoalbum.services;

import de.thg.photoalbum.model.Image;

import java.io.InputStream;

public interface ImageMetadataReader {

    Image readImageMetadata(InputStream inputStream, String originalName);

}
