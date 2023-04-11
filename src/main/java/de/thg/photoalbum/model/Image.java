package de.thg.photoalbum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(exclude = "version")
@ToString(exclude = "version")
public class Image implements Comparable<Image> {

    @Id
    @Column(columnDefinition = "varchar(32)")
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

    @Column(columnDefinition = "varchar(32)")
    @Getter
    @Setter
    private String filename;

    @Version
    private long version;

    @Transient
    @JsonIgnore
    @Getter
    @Setter
    private File tempFile;

    @Override
    public int compareTo(Image o) {
        return this.creationDate.compareTo(o.creationDate);
    }
}
