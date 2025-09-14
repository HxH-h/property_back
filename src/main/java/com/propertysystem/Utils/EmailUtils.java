package com.propertysystem.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {

    private static String from;
    private static JavaMailSender javaMailSender;
    
    @Value("${spring.mail.username}")
    public void setFrom(String from) {
        EmailUtils.from = from;
    }

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        EmailUtils.javaMailSender = javaMailSender;
    }

    public static void sendEmail(EmailDO email){

        SimpleMailMessage message = new SimpleMailMessage();
        //谁发的
        message.setFrom(from);
        //谁要接收
        message.setTo(email.getTo());
        //邮件标题
        message.setSubject(email.getSubject());
        //邮件内容
        message.setText(email.getContent());

        javaMailSender.send(message);

    }

}
