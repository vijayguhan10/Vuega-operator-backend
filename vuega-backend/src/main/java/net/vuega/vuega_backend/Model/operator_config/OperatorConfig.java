package net.vuega.vuega_backend.Model.operator_config;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String 
    licenseKey;
    @Column(name = "last_checked")
    private LocalDateTime lastChecked;
   
}
