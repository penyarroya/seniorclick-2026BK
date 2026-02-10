package es.jlrn.presentation.users.services.interfaces;


public interface IEmailService {
//
    public void sendVerificationEmail(String to, String code);

    void sendResetEmail(String to, String token);

    void sendResetCodeEmail(String to, String code);
}
