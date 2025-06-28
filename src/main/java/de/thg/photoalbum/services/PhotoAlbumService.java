package de.thg.photoalbum.services;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.repositories.ImageRepository;
import de.thg.photoalbum.util.LocalDateTimeConverter;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class PhotoAlbumService {

    private final Tika tika;

    @Inject
    public PhotoAlbumService(@Qualifier("metadata-extractor") ImageMetadataReader imageMetadataReader, ImageRepository imageRepository, LocalDateTimeConverter localDateTimeConverter) {
        this.imageMetadataReader = imageMetadataReader;
        this.imageRepository = imageRepository;
        this.localDateTimeConverter = localDateTimeConverter;
        tika = new Tika();
    }

    private static final Logger LOGGER = LogManager.getLogger(PhotoAlbumService.class);

    private static final Pattern pattern = Pattern.compile("\\d{8}_\\d{6}.jpg");
    private static final DecimalFormat df = new DecimalFormat("0000");

    private final ImageMetadataReader imageMetadataReader;
    private final ImageRepository imageRepository;
    private final LocalDateTimeConverter localDateTimeConverter;

    @Value("${photoalbum.extension:jpg}")
    private String EXTENSION;

    @Value("${photoalbum.prefix:PHOTO}")
    private String PREFIX;

    @Value("${photoalbum.start.count:1}")
    private int START_COUNT;

    public List<Image> createOrUpdatePhotoAlbum(AlbumParams albumParams) {

        List<String> sources = albumParams.sources();
        String target = albumParams.target();

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
            LOGGER.debug("created directory {}", targetDir.getAbsolutePath());
        }

        List<File> fileList = createFilteredFileList(albumParams);
        List<Image> result = new ArrayList<>();
        try {
            Map<Image, File> fileMap = createFileMap(fileList);
            Map<File, Image> targetFileMap = createTargetFiles(fileMap);

            if (!albumParams.debug()) {
                result = copyFiles(targetFileMap, targetDir);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }

        String msg = albumParams.debug() ? "simulated handling of " : "copied or updated ";
        LOGGER.info("{}{} pictures", msg, result.size());
        LOGGER.info("done");
        return result;
    }

    List<File> createFilteredFileList(AlbumParams params) {
        List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(params.target()).listFiles())));
        if(params.sources() != null) {
            for (File directory : params.sources().stream().map(File::new).toList()) {
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
                    LOGGER.debug("duplicate timestamp: {} - {}", file, fileMap.get(image));
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
        for (Map.Entry<Image, File> entry : fileMap.entrySet()) {
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
                LOGGER.info("{} is not a JPEG.", imageFile.getAbsolutePath());
                return false;
            }
        } catch (IOException e) {
            LOGGER.error("{} is not readable.", imageFile.getAbsolutePath());
            return false;
        }
        return true;
    }

    private boolean isValidFilename(String name) {
        return pattern.matcher(name).matches();
    }

    public List<Image> renameImageFiles(String... sourcePaths) throws IOException {
        List<Image> result = new ArrayList<>();
        for (String sourcePath : sourcePaths) {
            result.addAll(renameImageFiles(sourcePath));
        }
        return result;
    }

    private List<Image> renameImageFiles(String sourcePath) throws IOException {
        List<Image> result = new ArrayList<>();
        Path path = Paths.get(sourcePath);
        LOGGER.info("Path {} is {}a directory" , path.toString(), path.toFile().isDirectory() ? "" : "not ");
        for (File file : Objects.requireNonNull(path.toFile().listFiles(this::isJpg))) {
            LOGGER.debug("File: {}", file.getName());
            Optional<Image> image = analyseImage(new FileInputStream(file), file.getName());
            if(image.isPresent()) {
                Image thisImage = image.get();
                if(!isValidFilename(thisImage.getFilename())) {
                    thisImage.setFilename(localDateTimeConverter.toFilename(thisImage.getCreationDate()));
                    Files.move(file.toPath(), file.toPath().resolveSibling(thisImage.getFilename()), StandardCopyOption.REPLACE_EXISTING);
                }
                thisImage.setLastModified(LocalDateTime.now());
                result.add(thisImage);
            }
        }
        saveOrUpdateImages(result);
        return result;
    }

    private void saveOrUpdateImages(List<Image> images) {
        for (Image image : images) {
            Optional<Image> imageInDb = imageRepository.findFirstByFilename(image.getFilename());
            imageInDb.ifPresent(value -> image.setId(value.getId()));
            imageRepository.save(image);
        }
    }
}
