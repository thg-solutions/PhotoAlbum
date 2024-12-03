package de.thg.photoalbum.services;

import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.util.LocalDateTimeConverter;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@Component("apache-imaging")
public class ApacheImageReader implements ImageMetadataReader {

    private static final Logger LOGGER = LogManager.getLogger(ApacheImageReader.class);

    private LocalDateTimeConverter localDateTimeConverter;

    @Inject
    public ApacheImageReader(LocalDateTimeConverter localDateTimeConverter) {
        this.localDateTimeConverter = localDateTimeConverter;
    }

    @Override
    public Image readImageMetadata(InputStream fileInputStream, String originalName) {
        Image image = new Image();
        image.setFilename(originalName);
        try (fileInputStream) {
            ImageMetadata metadata = Imaging.getMetadata(fileInputStream, originalName);

            if (metadata instanceof JpegImageMetadata jpegMetadata) {
                image.setCreationDate(localDateTimeConverter.toLocalDateTime(jpegMetadata.findExifValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL)
                            .getValue().toString()));
                if (null != jpegMetadata.getExif() && null != jpegMetadata.getExif().getGpsInfo()) {
                    TiffImageMetadata.GpsInfo gpsInfo = jpegMetadata.getExif().getGpsInfo();
                    image.setLongitude(gpsInfo.getLongitudeAsDegreesEast());
                    image.setLatitude(gpsInfo.getLatitudeAsDegreesNorth());
                }
            }
        } catch (IOException e) {
            LOGGER.error("error handling image", e);
            return null;
        }
        return image;
    }
}
