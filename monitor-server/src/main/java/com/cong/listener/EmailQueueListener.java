package com.cong.listener;

import com.cong.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "emailQueue")
public class EmailQueueListener {
  @Resource
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String username;

  @RabbitHandler
  public void sendMailMessage(Map<String,Object> data) {
    String email = (String) data.get("email");
    Integer code = (Integer) data.get("code");
    String type = (String) data.get("type");
    SimpleMailMessage message = switch (type) {
      case Const.RESET_EMAIL ->
        createSimpleMailMessage("Password Reset Code",
            "Your Password Reset Code: " + code + "\n" + "\n" + "Enter this code on the password reset page to set a new password.",
            email);
      default -> null;
    };
    if (message!=null) {
      mailSender.send(message);
    }
  }

  private SimpleMailMessage createSimpleMailMessage(String title, String content, String to) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(title);
    message.setText(content);
    message.setFrom(username);
    return message;
  }
}
