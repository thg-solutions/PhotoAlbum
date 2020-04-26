package de.thg.photoalbum.services;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhotoAlbumService {

    @Autowired
    public PhotoAlbumService(@Qualifier("metadata-extractor") ImageMetadataReader imageMetadataReader) {
        this.imageMetadataReader = imageMetadataReader;
    }

    private static final Logger LOGGER = LogManager.getLogger(PhotoAlbumService.class);

    private ImageMetadataReader imageMetadataReader;

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
            LOGGER.info("Target diretory not set, skipping");
            return new ArrayList<>();
        }
        if (sources == null || sources.isEmpty()) {
            LOGGER.info("Source directory not set");
        }

        File targetDir = new File(target);
        if(!targetDir.exists()) {
            targetDir.mkdirs();
            LOGGER.info("created directory " + targetDir.getAbsolutePath());
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
            e.printStackTrace();
        }

        String msg = albumParams.isDebug() ? "simulated handling of " : "copied or updated ";
        LOGGER.info(msg + result.size() + " pictures");
        LOGGER.info("done");
        return result;
    }

    List<File> createFilteredFileList(AlbumParams params) {
        List<File> fileList = new ArrayList<>();
        fileList.addAll(Arrays.asList(new File(params.getTarget()).listFiles()));
        if(params.getSources() != null) {
            for (File directory : params.getSources().stream().map(File::new).collect(Collectors.toList())) {
                if (!directory.isDirectory()) {
                    continue;
                }
                fileList.addAll(Arrays.asList(directory.listFiles()));
            }
        }
        return fileList.stream().filter(file -> file.isFile() && file.getName().endsWith("JPG")).collect(Collectors.toList());
    }

    Map<Image, File> createFileMap(List<File> fileList) throws IOException {
        Map<Image, File> fileMap = new TreeMap<>();
        for (File file : fileList) {
            Image image = imageMetadataReader.readImageMetadata(file);
            if (image != null) {
                if (fileMap.containsKey(image)) {
                    LOGGER.debug("duplicate timestamp: " + file + " - " + fileMap.get(image));
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
        for (Image key : fileMap.keySet()) {
            String filename = PREFIX + df.format(count) + "." + EXTENSION;
            File newFileInTempDir = new File(tempDir, filename);
            key.setFilename(filename);
            key.setLastModified(new Date());
            key.setTempFile(newFileInTempDir);
            targetFileMap.put(fileMap.get(key), key);
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
            for (File key : filesToCopy.keySet()) {
                FileUtils.copyFile(key, filesToCopy.get(key).getTempFile());
                result.add(filesToCopy.get(key));
            }
            File tempDir = filesToCopy.get(filesToCopy.keySet().iterator().next()).getTempFile().getParentFile();
            FileUtils.cleanDirectory(targetDir);
            FileUtils.copyDirectory(tempDir, targetDir);
            FileUtils.deleteQuietly(tempDir);
        }
        return result;
    }

    public Optional<Image> analyseImage(String filename) {
        return Optional.ofNullable(imageMetadataReader.readImageMetadata(new File(filename)));
    }

}
