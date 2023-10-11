package de.thg.photoalbum.services;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;

@Service
public class PhotoAlbumService {

    private Tika tika;

    @Inject
    public PhotoAlbumService(@Qualifier("metadata-extractor") ImageMetadataReader imageMetadataReader) {
        this.imageMetadataReader = imageMetadataReader;
        tika = new Tika();
    }

    private static final Logger LOGGER = LogManager.getLogger(PhotoAlbumService.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
    private static final Pattern pattern = Pattern.compile("\\d{8}_\\d{6}.jpg");


    private final ImageMetadataReader imageMetadataReader;

    @Value("${photoalbum.extension}")
    private String EXTENSION;

    @Value("${photoalbum.prefix}")
    private String PREFIX;

    @Value("${photoalbum.start.count}")
    private int START_COUNT;

    public List<Image> createOrUpdatePhotoAlbum(AlbumParams albumParams) {

        List<String> sources = albumParams.getSources();
        String target = albumParams.getTarget();

        if (target == null) {
            LOGGER.info("Target directory not set, skipping");
            return new ArrayList<>();
        }
        if (sources == null || sources.isEmpty()) {
            LOGGER.info("Source directory not set or empty");
        }

        File targetDir = new File(target);
        if(!targetDir.exists()) {
            targetDir.mkdirs();
            LOGGER.debug("created directory " + targetDir.getAbsolutePath());
        }

        List<File> fileList = createFilteredFileList(albumParams);
        List<Image> result = new ArrayList<>();
        try {
            Map<Image, File> fileMap = createFileMap(fileList);
            Map<File, Image> targetFileMap = createTargetFiles(fileMap);

            if (!albumParams.isDebug()) {
                result = copyFiles(targetFileMap, targetDir);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }

        String msg = albumParams.isDebug() ? "simulated handling of " : "copied or updated ";
        LOGGER.info(msg + result.size() + " pictures");
        LOGGER.info("done");
        return result;
    }

    List<File> createFilteredFileList(AlbumParams params) {
        List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(params.getTarget()).listFiles())));
        if(params.getSources() != null) {
            for (File directory : params.getSources().stream().map(File::new).toList()) {
                if (!directory.isDirectory()) {
                    continue;
                }
                fileList.addAll(Arrays.asList(Objects.requireNonNull(directory.listFiles())));
            }
        }
        return fileList.stream().filter(this::isJpg).toList();
    }

    Map<Image, File> createFileMap(List<File> fileList) throws IOException {
        Map<Image, File> fileMap = new TreeMap<>();
        for (File file : fileList) {
            Image image = imageMetadataReader.readImageMetadata(new FileInputStream(file), file.getName());
            if (image != null) {
                if (fileMap.containsKey(image)) {
                    LOGGER.debug("duplicate timestamp: {0} - {1}", file, fileMap.get(image));
                } else {
                    fileMap.put(image, file);
                }
            }
        }
        return fileMap;
    }

    Map<File, Image> createTargetFiles(Map<Image, File> fileMap) throws IOException {
        Map<File, Image> targetFileMap = new HashMap<>();
        File tempDir = createTempDir("PHOTOS");
        tempDir.mkdir();
        FileUtils.forceDeleteOnExit(tempDir);
        int count = START_COUNT;
        DecimalFormat df = new DecimalFormat("0000");
        for (Map.Entry<Image, File> entry : fileMap.entrySet()) {
            // new Filename: yyyyMMdd_HHmmss
            String filename = PREFIX + df.format(count) + "." + EXTENSION;
            File newFileInTempDir = new File(tempDir, filename);
            entry.getKey().setFilename(filename);
            entry.getKey().setLastModified(LocalDateTime.now());
            entry.getKey().setTempFile(newFileInTempDir);
            targetFileMap.put(entry.getValue(), entry.getKey());
            count++;
        }
        return targetFileMap;
    }

    private File createTempDir(String prefix) throws IOException {
        File tempFile = File.createTempFile(prefix, "");
        File systemTempDir = tempFile.getParentFile();
        String dirName = tempFile.getName();
        tempFile.delete();
        File tempDir = new File(systemTempDir, dirName);
        tempDir.mkdir();
        return tempDir;
    }

    List<Image> copyFiles(Map<File, Image> filesToCopy, File targetDir) throws IOException {
        List<Image> result = new ArrayList<>();
        if(!filesToCopy.isEmpty()){
            for (Map.Entry<File, Image> entry : filesToCopy.entrySet()) {
                FileUtils.copyFile(entry.getKey(), entry.getValue().getTempFile());
                result.add(entry.getValue());
            }
            File tempDir = filesToCopy.get(filesToCopy.keySet().iterator().next()).getTempFile().getParentFile();
            FileUtils.cleanDirectory(targetDir);
            FileUtils.copyDirectory(tempDir, targetDir);
            FileUtils.deleteQuietly(tempDir);
        }
        return result;
    }

    public Optional<Image> analyseImage(InputStream inputStream, String originalName) {
        return Optional.ofNullable(imageMetadataReader.readImageMetadata(inputStream, originalName));
    }

    private boolean isJpg(File imageFile) {
        try {
            if (!"image/jpeg".equals(tika.detect(imageFile))) {
                LOGGER.info("{0} is not a JPEG.", imageFile.getAbsolutePath());
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("{0} is not readable.", imageFile.getAbsolutePath());
            return false;
        }
        return true;
    }

    private boolean isValidFilename(String name) {
        return pattern.matcher(name).matches();
    }

    private String reformatDate(String dateFromMetadata) {
        LocalDateTime ldt = LocalDateTime.parse(dateFromMetadata, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return ldt.format(formatter);
    }

}
