package de.thg.photoalbum.repositories;

import de.thg.photoalbum.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

    List<Image> findByFilenameStartingWith(String name);

    Optional<Image>  findByFilename(String name);

}
