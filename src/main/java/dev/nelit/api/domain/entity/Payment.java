package dev.nelit.api.domain.entity;

import dev.nelit.api.enums.PaymentGateway;
import dev.nelit.api.enums.PaymentStatus;
import dev.nelit.api.enums.PaymentType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@Table(name = "payments")
public class Payment {
    @Id
    @Column("id_payment")
    private Long idPayment;

    @Column("id_user")
    private Long idUser;

    @Column("gateway")
    private PaymentGateway gateway;

    @Column("gateway_payment_id")
    private String gatewayPaymentId;

    @Column("amount")
    private BigDecimal amount;

    @Column("currency")
    private String currency;

    @Column("status")
    private PaymentStatus status;

    @Column("type")
    private PaymentType type;

    @Column("created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();
}
