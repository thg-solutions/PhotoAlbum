package de.thg.photoalbum.repositories;

import de.thg.photoalbum.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {

    List<Image> findByFilenameStartingWith(String name);

    List<Image> findByFilename(String name);

    Optional<Image> findFirstByFilename(String filename);
}
