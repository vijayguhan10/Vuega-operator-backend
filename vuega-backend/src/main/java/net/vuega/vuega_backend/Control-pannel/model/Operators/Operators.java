package net.vuega.control_plane.model.operators;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.vuega.control_plane.util.OperatorStatus;

@Entity
@Table(name = "operators")
public class Operators {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long operatorId;

    // @NotBlank(message = "Operator name cannot be null")
    private String operatorName;

    // @NotBlank(message = "Company name cannot be null")
    private String companyName;

    @Enumerated(EnumType.STRING)
    private OperatorStatus status;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public OperatorStatus getStatus() {
        return status;
    }

    public void setStatus(OperatorStatus status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long operatorId;
        private String operatorName;
        private String companyName;
        private OperatorStatus status;

        public Builder operatorId(Long operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public Builder operatorName(String operatorName) {
            this.operatorName = operatorName;
            return this;
        }

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder status(OperatorStatus status) {
            this.status = status;
            return this;
        }

        public Operators build() {
            Operators entity = new Operators();
            entity.setOperatorId(operatorId);
            entity.setOperatorName(operatorName);
            entity.setCompanyName(companyName);
            entity.setStatus(status);
            return entity;
        }
    }

}
