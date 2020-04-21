package de.thg.photoalbum.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class AlbumParams {

	private String target;

	private List<String> sources;

	private boolean debug;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<String> getSources() {
		return sources;
	}

	public void setSources(List<String> sources) {
		this.sources = sources;
	}
	
	public void addSource(String source) {
		if(this.sources == null) {
			this.sources = new ArrayList<String>();
		}
		this.sources.add(source);
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
