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
