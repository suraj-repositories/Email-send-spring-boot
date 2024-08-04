# Email-send-spring-boot

### Steps

- firstly you need to create app-password form your gmail account settings
- if the app-password is not visible to you. make sure turn on 2-step verification and search for 'App password' in the search box of google account

- once you have app-password and your username you are good to go
- add the following dependency in your pom.xml file 

	```html
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-mail</artifactId>
	</dependency>
	```

-  configure your application.yml file with the following properties

	```yml
	spring: 
	  mail:
	    username: your_email@gmail.com
	    host: smtp.gmail.com
	    port: 587
	    password: xxxx xxxx xxxx xxxx  # your app password here(12 digits)
	    properties:
	      mail:
	        transport:
	          protocol: smtp
	        smtp:
	          auth: true
	          starttls:
	             required: true
	             enabled: true
	        debug: true
	```
	
- configure a been for java mail sender 

	```java
	package com.on31july.EmailSendExample.config;
	
	import java.util.Properties;
	
	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.mail.javamail.JavaMailSender;
	import org.springframework.mail.javamail.JavaMailSenderImpl;
	
	@Configuration
	public class MailConfig {
		
		@Value("${spring.mail.host}")
		private String HOST;
		
		@Value("${spring.mail.port}")
		private String PORT;
		
		@Value("${spring.mail.username}")
		private String USERNAME;
		
		@Value("${spring.mail.password}")
		private String PASSWORD;
		
		@Value("${spring.mail.properties.mail.transport.protocol}")
		private String PROTOCOL;
		
		@Value("${spring.mail.properties.mail.smtp.auth}")
		private String AUTH;
		
		@Value("${spring.mail.properties.mail.smtp.starttls.required}")
		private String STARTTLS_REQUIRED;
		
		@Value("${spring.mail.properties.mail.smtp.starttls.enabled}")
		private String STARTTLS_ENABLED;
		
		@Value("${spring.mail.properties.mail.debug}")
		private String DEBUG;
		
		public static final String EMAIL_TEMPLATE_ENCODEING = "UTF-8";
		
		@Bean
		JavaMailSender getJavaMailSender() {
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost(HOST);
			mailSender.setPort(Integer.parseInt(PORT));
			mailSender.setUsername(USERNAME);
			mailSender.setPassword(PASSWORD);
			
			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.transport.protocol", PROTOCOL);
			props.put("mail.smtp.auth", AUTH);
			props.put("mail.smtp.starttls.enabled", STARTTLS_ENABLED);
			props.put("mail.smtp.starttls.required", STARTTLS_REQUIRED);
			props.put("mail.debug", DEBUG);
			
			return mailSender;
		}
	}
	```
	
- Create a mail sender service in java to send emails and also attachments with email : if you want to send attachments you need to put your attachments on `src/main/resources/static` directory

	```java
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
	```

	
- lastly create a controller to handle the email sending 

	```java
	package com.on31july.EmailSendExample.controller;
	
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Controller;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.servlet.mvc.support.RedirectAttributes;
	import com.on31july.EmailSendExample.services.MailSenderService;
	
	@Controller
	public class SendEmailController {
		
		@Autowired
		private MailSenderService mailSenderService;
		
		private Logger LOGGER = LoggerFactory.getLogger(SendEmailController.class);
		
		@GetMapping("/")
		public String sendEmailPage() {		
			return "send-form";
		}
		
		@PostMapping("/")
		public String sendEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
			
			try {
				LOGGER.info("Email : {}", email);
				mailSenderService.sendHtmlMail(email);
				redirectAttributes.addFlashAttribute("success", "Email sent successfully!");
				
			}catch(Exception e){
				LOGGER.error("EMAIL SEND FAIL !");
				redirectAttributes.addFlashAttribute("error", "Email not send! any error may occure!");
			}
			
			return "redirect:/";
		}
		
	}
	
	```
	
- One thing you might to note the way of using dynamic data and images in `/Email-send-spring-boot/src/main/resources/templates/email_html.html`

	```html
	<p>
	   <img src="Logo.png" alt="Logo image" th:src="|cid:${logo}|">
	</p>
	<div>Welcome By <th:block th:text="${username}"></th:block></div>
	```

### Files where i made changes

- /Email-send-spring-boot/pom.xml
- /Email-send-spring-boot/src/main/resources/application.yml
- /Email-send-spring-boot/src/main/java/com/on31july/EmailSendExample/config/MailConfig.java
- /Email-send-spring-boot/src/main/java/com/on31july/EmailSendExample/controller/SendEmailController.java
- /Email-send-spring-boot/src/main/java/com/on31july/EmailSendExample/services/MailSenderService.java
- /Email-send-spring-boot/src/main/resources/templates/email_html.html
- /Email-send-spring-boot/src/main/resources/templates/send-form.html

=============== static files ==================
- /Email-send-spring-boot/src/main/resources/static/attachment1.txt
- /Email-send-spring-boot/src/main/resources/static/attachment2.pdf
- /Email-send-spring-boot/src/main/resources/static/Logo.png


<br />
<br />
<p align="center">⭐️ Star my repositories if you find it helpful.</p>
<br />
