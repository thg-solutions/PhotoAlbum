package de.thg.photoalbum.controller;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.repositories.ImageRepository;
import de.thg.photoalbum.services.PhotoAlbumService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
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
        return imageRepository.findAll();
    }

    @GetMapping("/photos/search/namestartingwith")
    public List<Image> getImageByNameStartingWith(@RequestParam String startswith) {
        return imageRepository.findByFilenameStartingWith(startswith);
    }

    @GetMapping("/photos/search/creationdate")
    public ResponseEntity<Image> getImageByCreationDate(@RequestParam String creationDate) {
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

    @PostMapping("analyse")
    @CrossOrigin(origins = {"${cross.origins}"})
    public ResponseEntity<Image> analyseImage(@RequestParam("file") MultipartFile fileToAnalyse) throws IOException {
        Optional<Image> result = service.analyseImage(fileToAnalyse.getInputStream(), fileToAnalyse.getOriginalFilename());
        fileToAnalyse.getInputStream().close();
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleFileNotFound() {
        return ResponseEntity.notFound().build();
    }

}
