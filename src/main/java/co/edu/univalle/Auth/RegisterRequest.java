package co.edu.univalle.Auth;

import co.edu.univalle.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String code;
    private String password;
    private String confirmPassword;

}
