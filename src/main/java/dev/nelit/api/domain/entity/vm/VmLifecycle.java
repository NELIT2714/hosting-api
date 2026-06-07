package dev.nelit.api.domain.entity.vm;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "vm_lifecycle")
public class VmLifecycle {

    @Id
    @Column("id_lifecycle")
    private Long idLifecycle;

    @Column("id_vm")
    private Long idVm;

    @Column("blocked_at")
    @Builder.Default()
    private Instant blockedAt = Instant.now();

    @Column("delete_at")
    private Instant deleteAt;

}
