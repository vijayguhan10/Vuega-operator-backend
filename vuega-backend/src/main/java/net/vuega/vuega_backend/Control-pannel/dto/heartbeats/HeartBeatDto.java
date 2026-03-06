package net.vuega.control_plane.dto.heartbeats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartBeatDto {

    private Long heartbeatId;
    private Long operatorId;
    private Integer busCount;
    private Integer routeCount;
    private Integer tripCount;
}