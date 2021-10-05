package de.thg.photoalbum.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AlbumParams {

	private String target;

	private List<String> sources;

	private boolean debug;

	public List<String> getSources() {
		if (sources == null) {
			sources = new ArrayList<>();
		}
		return sources;
	}

}
