package spring.web.webApp.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("EmailService")
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    public String sendSimpleEmailMessage(String to, String subject, String body){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        try {
            javaMailSender.send(simpleMailMessage);
            return "200";
        }catch (Exception e){
            System.out.println("error on sending message: "+e.getMessage());
        }
        return "";
    }
}
