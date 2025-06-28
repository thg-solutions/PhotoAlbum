package de.thg.photoalbum.model;

import java.util.List;

public record AlbumParams(String target, List<String> sources, boolean debug) {

}
