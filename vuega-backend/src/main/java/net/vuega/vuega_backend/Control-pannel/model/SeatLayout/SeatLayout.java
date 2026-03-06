package net.vuega.control_plane.model.seatlayout;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "seat_layouts")
public class SeatLayout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "layout_id")
    private Long layoutId;

    @Column(name = "layout_name", nullable = false)
    private String layoutName;

    @Column(name = "seat_count", nullable = false)
    private int seatCount;

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }
}
