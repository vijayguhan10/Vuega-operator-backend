package net.vuega.vuega_backend.Model.operator_config;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operator_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "license_key", nullable = false)
    private String licenseKey;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;
}
