package net.vuega.vuega_backend.Control_pannel.dto.heartbeats;

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