package net.vuega.control_plane.dto.operatorauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.control_plane.util.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Register {
    private Long operatorId;
    private String licenceId;
    private String name;
    private String email;
    private String password;
    private Role role;
}