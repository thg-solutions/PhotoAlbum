package de.thg.photoalbum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.File;
import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
public class Image implements Comparable<Image> {

    @Id
    @Getter
    private String id;

    @Getter
    @Setter
    private String creationDate;

    @Getter
    @Setter
    private Double latitude;

    @Getter
    @Setter
    private Double longitude;

    @Getter
    @Setter
    private LocalDateTime lastModified;

    @Getter
    @Setter
    private String filename;

    @Transient
    @JsonIgnore
    @Getter
    @Setter
    private File tempFile;

    public Image() {

    }

    public Image(String filename, String creationDate) {
        this.filename = filename;
        this.creationDate = creationDate;
    }

    @Override
    public int compareTo(Image o) {
        return this.creationDate.compareTo(o.creationDate);
    }
}
