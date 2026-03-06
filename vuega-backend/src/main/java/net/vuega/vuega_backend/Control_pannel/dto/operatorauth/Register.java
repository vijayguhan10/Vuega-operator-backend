package net.vuega.vuega_backend.Control_pannel.dto.operatorauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Control_pannel.util.Role;

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