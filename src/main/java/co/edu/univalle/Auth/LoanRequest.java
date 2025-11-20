package co.edu.univalle.Auth;

import lombok.Data;

@Data
public class LoanRequest {
    String userCode;
    Long bookId;
}
