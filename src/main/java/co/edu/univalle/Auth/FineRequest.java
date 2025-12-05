package co.edu.univalle.Auth;


import lombok.Data;

@Data
public class FineRequest {
    Long fineId;
    String justification;
    Double valor;
}
