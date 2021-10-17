package de.thg.photoalbum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

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

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(timezone = "Europe/Berlin")
    @Getter
    @Setter
    private Date lastModified;

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
