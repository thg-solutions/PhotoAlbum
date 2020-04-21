package de.thg.photoalbum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

@Entity
public class Image implements Comparable<Image> {

    @Id
    @Column(columnDefinition = "varchar(32)")
    private String creationDate;

    private Double latitude;

    private Double longitude;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = "Europe/Berlin")
    private Date lastModified;

    @Column(columnDefinition = "varchar(32)")
    private String filename;

    @Version
    private long version;

    @Transient
    @JsonIgnore
    private File tempFile;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public File getTempFile() {
        return tempFile;
    }

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int compareTo(Image o) {
        return this.getCreationDate().compareTo(o.getCreationDate());
    }

}
