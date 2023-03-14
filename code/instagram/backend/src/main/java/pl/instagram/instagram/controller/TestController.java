package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestController {

    private final JavaMailSender mailSender;

    @GetMapping(value = "/public")
    public String publicEndpoint() {
        return "All good. You DO NOT need to be authenticated to call /api/public.";
    }

    @GetMapping(value = "/private")
    public String privateEndpoint() {
        return "All good. You can see this because you are Authenticated.";
    }

    @GetMapping(value = "/private-scoped")
    public String privateScopedEndpoint() {
        return "All good. You can see this because you are Authenticated with a Token granted the 'read:messages' scope";
    }

    @GetMapping(value = "/email")
    public void sendEmail(){

        String to = "jdywan@wp.pl";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Wiadomosc");
        message.setText(":D");

        mailSender.send(message);
    }
}
