package com.on31july.EmailSendExample.services;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailSenderService {
	
	@Value("${spring.mail.username}")
	private String USERNAME;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private SpringTemplateEngine templateEngine;
	
	
	public void sendHtmlMail(String to) throws MessagingException{
		MimeMessage message = javaMailSender.createMimeMessage();
		Context context = new Context();
		context.setVariable("username", USERNAME);
		context.setVariable("logo", "logo");
		String process = templateEngine.process("email_html.html", context);
		
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
		helper.setTo(to);
		helper.setFrom(new InternetAddress(USERNAME));
		helper.setSubject("Orange Team support");;
		helper.setText(process, true);
		helper.addInline("logo", new ClassPathResource("static/Logo.png"));
		helper.addAttachment("attachment1.txt", new ClassPathResource("static/attachment1.txt"));
		helper.addAttachment("attachment2.pdf", new ClassPathResource("static/attachment2.pdf"));
		
		javaMailSender.send(message);
	}
	
}
