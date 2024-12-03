package de.thg.photoalbum.services;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.util.LocalDateTimeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component("metadata-extractor")
public class MetadataExtractorReader implements ImageMetadataReader {

    private static final Logger LOGGER = LogManager.getLogger(MetadataExtractorReader.class);

    private LocalDateTimeConverter localDateTimeConverter;

    @Inject
    public MetadataExtractorReader(LocalDateTimeConverter localDateTimeConverter) {
        this.localDateTimeConverter = localDateTimeConverter;
    }


    @Override
    public Image readImageMetadata(InputStream inputStream, String originalName) {
        Image image = new Image();
        image.setFilename(originalName);
        try (inputStream) {
            Metadata metadata = JpegMetadataReader.readMetadata(inputStream, List.of(new ExifReader()));
            if(metadata.getDirectoryCount() == 0) {
                return null;
            }
            Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            image.setCreationDate(localDateTimeConverter.toLocalDateTime(directory.getString(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED)));
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null) {
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null) {
                    image.setLatitude(geoLocation.getLatitude());
                    image.setLongitude(geoLocation.getLongitude());
                }
            }
        } catch (JpegProcessingException | IOException e) {
            LOGGER.error("error handling image", e);
            return null;
        }
        return image;
    }
}
