package dev.nelit.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@Table(name = "plans")
public class Plan {

    @Id
    @Column("id_plan")
    private Long idPlan;

    @Column("plan_name")
    private String planName;

    @Column("ram_mb")
    private Integer ramMb;

    @Column("vcpus")
    private Integer vcpus;

    @Column("disk_gb")
    private Integer diskGb;

    @Column("price_per_month")
    private BigDecimal pricePerMonth;

    @Column("max_count")
    private Integer maxCount;

    @Column("max_uplink_mbps")
    private Integer maxUplinkMbps;

    @Column("is_active")
    private Boolean isActive;

}
