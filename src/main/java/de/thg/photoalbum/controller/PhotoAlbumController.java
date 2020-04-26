package de.thg.photoalbum.controller;

import de.thg.photoalbum.model.AlbumParams;
import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.repositories.ImageRepository;
import de.thg.photoalbum.services.PhotoAlbumService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/photoalbum")
public class PhotoAlbumController {

    private static final Logger LOGGER = LogManager.getLogger(PhotoAlbumController.class);

    @Autowired
    private PhotoAlbumService service;

    @Autowired
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
    public ResponseEntity<Image> analyseImage(@RequestParam("filename") String fileToAnalyse) {
        Optional<Image> result = service.analyseImage(fileToAnalyse);
        System.out.println(fileToAnalyse);
        if(result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
