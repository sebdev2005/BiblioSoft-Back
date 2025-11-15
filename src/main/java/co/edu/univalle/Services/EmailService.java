package co.edu.univalle.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    JavaMailSender javaMailSender;

    public void sendMail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("javier.santacruz@correounivalle.edu.co");
        message.setTo(toEmail);
        message.setSubject("Restablecimiento de contraseña");
        message.setText(
                "Hola,\n\nHemos recibido una solicitud para restablecer tu contraseña.\n" +
                        "Haz clic en el siguiente enlace para cambiarla:\n\n" +
                        resetLink + "\n\n" +
                        "Si no solicitaste este cambio, ignora este mensaje."
        );

        javaMailSender.send(message);
    }
}
