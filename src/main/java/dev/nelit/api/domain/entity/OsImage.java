package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table(name = "os_images")
public class OsImage {

    @Id
    @Column("id_os_image")
    private Long idOsImage;

    @Column("image_name")
    private String imageName;

    @Column("file_name")
    private String fileName;

}
