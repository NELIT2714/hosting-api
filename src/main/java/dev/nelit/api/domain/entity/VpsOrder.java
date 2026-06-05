package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "vps_orders")
public class VpsOrder {

    @Id
    @Column("id_vps_order")
    private Long idOrder;

    @Column("id_payment")
    private Long idPayment;

    @Column("id_plan")
    private Long idPlan;

    @Column("id_os_image")
    private Long idOsImage;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
