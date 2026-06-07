package dev.nelit.api.domain.entity.vps;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "vps_renewal_orders")
public class VpsRenewalOrder {

    @Id
    @Column("id_renewal_order")
    private Long idRenewalOrder;

    @Column("id_vm")
    private Long idVm;

    @Column("id_payment")
    private Long idPayment;

    @Column("days")
    @Builder.Default
    private Integer days = 30;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
