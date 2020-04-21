package de.thg.photoalbum.services;

import de.thg.photoalbum.model.Image;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component("apache-imaging")
public class ApacheImageReader implements ImageMetadataReader {

    private static final Logger LOGGER = LogManager.getLogger(ApacheImageReader.class);

    @Override
    public Image readImageMetadata(File file) throws IOException {
        Image image = null;
        try {
            ImageMetadata metadata = Imaging.getMetadata(file);

            if (metadata instanceof JpegImageMetadata) {
                image = new Image();
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                image.setCreationDate(jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_DATE_TIME)
                        .getValue().toString());
                if (null != jpegMetadata.getExif() && null != jpegMetadata.getExif().getGPS()) {
                    TiffImageMetadata.GPSInfo gpsInfo = jpegMetadata.getExif().getGPS();
                    image.setLongitude(gpsInfo.getLongitudeAsDegreesEast());
                    image.setLatitude(gpsInfo.getLatitudeAsDegreesNorth());
                }
                image.setFilename(file.getName());
            }
        } catch (ImageReadException e) {
            LOGGER.error("error reading image metadata");
        }
        return image;
    }
}
