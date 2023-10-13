package de.thg.photoalbum.services;

import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.util.LocalDateTimeConverter;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
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
                image.setCreationDate(localDateTimeConverter.toLocalDateTime(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_DATE_TIME)
                            .getValue().toString()));
                if (null != jpegMetadata.getExif() && null != jpegMetadata.getExif().getGPS()) {
                    TiffImageMetadata.GPSInfo gpsInfo = jpegMetadata.getExif().getGPS();
                    image.setLongitude(gpsInfo.getLongitudeAsDegreesEast());
                    image.setLatitude(gpsInfo.getLatitudeAsDegreesNorth());
                }
            }
        } catch (ImageReadException | IOException e) {
            LOGGER.error("error handling image", e);
            return null;
        }
        return image;
    }
}
