package de.thg.photoalbum.controller;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.repositories.ImageRepository;
import de.thg.photoalbum.services.PhotoAlbumService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/photoalbum")
public class PhotoAlbumController {

    private static final Logger LOGGER = LogManager.getLogger(PhotoAlbumController.class);

    @Inject
    private PhotoAlbumService service;

    @Inject
    private ImageRepository imageRepository;

    @GetMapping("/photos")
    public List<Image> getAllImages() {
        List<Image> imageList = imageRepository.findAll();
        Collections.sort(imageList);
        return imageList;
    }

    @GetMapping("/photos/search/namestartingwith")
    public List<Image> getImageByNameStartingWith(@RequestParam String startswith) {
        return imageRepository.findByFilenameStartingWith(startswith);
    }

    @GetMapping("/photos/search/creationdate")
    public ResponseEntity<Image> getImageByCreationDate(@RequestParam("creationDate") String creationDate) {
        Optional<Image> result = imageRepository.findById(creationDate);
        if(result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public List<Image> createOrUpdatePhotoAlbum(@RequestBody  AlbumParams params) {
        List<Image> imageList = service.createOrUpdatePhotoAlbum(params);
        imageRepository.saveAll(imageList);
        return imageList;
    }

    @PostMapping(value = "analyze_image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Image> analyseImage(@RequestParam("file") MultipartFile fileToAnalyse) throws IOException {
        Optional<Image> result;
        try(InputStream inputStream = fileToAnalyse.getInputStream()) {
            result = service.analyseImage(fileToAnalyse.getInputStream(), fileToAnalyse.getOriginalFilename());
        }
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("renameImages")
    ResponseEntity<List<Image>> renameImages(@RequestParam("directory") String[] directory) throws IOException {
        List<Image> result = service.renameImageFiles(directory);
        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleFileNotFound() {
        return ResponseEntity.notFound().build();
    }

}
