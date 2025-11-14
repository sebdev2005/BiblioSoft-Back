package co.edu.univalle.Auth;

import co.edu.univalle.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    String username;
    String code;
    String password;
    String firstname;
    String lastname;
    String country;
    String email;
    Role role;
}
