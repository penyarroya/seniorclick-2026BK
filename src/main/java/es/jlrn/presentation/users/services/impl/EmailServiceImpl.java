// package es.jlrn.presentation.users.services.impl;

// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.stereotype.Service;

// import es.jlrn.presentation.users.services.interfaces.IEmailService;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class EmailServiceImpl implements IEmailService {
// //
//     private final JavaMailSender mailSender;

//     @Override
//     public void sendVerificationEmail(String to, String code) {
//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setTo(to);
//         message.setSubject("Verificación de cuenta");
//         message.setText("Tu código de verificación es: " + code);

//         mailSender.send(message);
//     }

//     @Override
//     public void sendResetEmail(String to, String token) {
//         // Genera el link completo solo aquí
//         String resetLink = "http://localhost:4200/reset-password?token=" + token;

//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setTo(to);
//         message.setSubject("Restablecer contraseña");
//         message.setText("Has solicitado restablecer tu contraseña.\n\n"
//                         + "Haz clic en el siguiente enlace para crear una nueva contraseña:\n"
//                         + resetLink);

//         mailSender.send(message);
//     }

// }


package es.jlrn.presentation.users.services.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import es.jlrn.presentation.users.services.interfaces.IEmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verificación de cuenta");
        message.setText("Tu código de verificación es: " + code);
        mailSender.send(message);
    }

    @Override
    public void sendResetEmail(String to, String token) {
        String resetLink = "http://localhost:4200/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Restablecer contraseña");
        message.setText("Has solicitado restablecer tu contraseña.\n\n" +
                        "Haz clic en el siguiente enlace para crear una nueva contraseña:\n" +
                        resetLink);
        mailSender.send(message);
    }

    // -------------------- NUEVO MÉTODO PARA OTP --------------------
    @Override
    public void sendResetCodeEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Restablecer contraseña");
        message.setText("Has solicitado restablecer tu contraseña.\n\n" +
                        "Tu código de verificación es: " + code + "\n" +
                        "Este código expira en 3 minutos.");
        mailSender.send(message);
    }
}
