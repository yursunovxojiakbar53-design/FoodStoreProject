package org.example.project.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.project.extra.AbstractEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment extends AbstractEntity {

    private String fileName;

    private Long size;

    private String contentType;
    private String path;
}
