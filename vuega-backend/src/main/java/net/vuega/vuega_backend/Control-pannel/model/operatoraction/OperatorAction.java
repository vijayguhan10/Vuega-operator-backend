package net.vuega.control_plane.model.operatoraction;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.vuega.control_plane.util.OperatorActionType;

@Entity
@Table(name = "actions")
@Getter
@Setter
public class OperatorAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long actionId;

    @Column(name = "operator_id", nullable = false)
    private Long operatorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperatorActionType action;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}