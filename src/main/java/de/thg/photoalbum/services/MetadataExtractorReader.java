package de.thg.photoalbum.services;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.GpsDirectory;
import de.thg.photoalbum.model.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Component("metadata-extractor")
public class MetadataExtractorReader implements ImageMetadataReader {

    private static final Logger LOGGER = LogManager.getLogger(MetadataExtractorReader.class);

    @Override
    public Image readImageMetadata(File file) throws IOException {
        Image image = new Image();
        image.setFilename(file.getName());
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(file, Arrays.asList(new ExifReader()));
            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            image.setCreationDate(directory.getDescription(ExifIFD0Directory.TAG_DATETIME));
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            GeoLocation geoLocation = gpsDirectory.getGeoLocation();
            if(geoLocation != null) {
                image.setLatitude(geoLocation.getLatitude());
                image.setLongitude(geoLocation.getLongitude());
            }
        } catch (JpegProcessingException e) {
            LOGGER.error("error reading image metadata", e);
        }
        return image;
    }
}
