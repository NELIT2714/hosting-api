package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table(name = "receipts")
public class Receipt {

    @Id
    @Column("id_receipt")
    private Long idReceipt;

    @Column("id_payment")
    private Long idPayment;

    @Column("number")
    private String number;

    @Column("buyer_name")
    private String buyerName;

    @Column("buyer_address")
    private String buyerAddress;

    @Column("service_description")
    private String serviceDescription;

    @Column("issued_at")
    @Builder.Default
    private Instant issuedAt = Instant.now();
}