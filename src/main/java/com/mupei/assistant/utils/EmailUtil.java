package com.mupei.assistant.utils;

import java.io.File;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
	@Autowired
	private JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String from;

	/**
	 * 发送普通邮箱
	 *
	 * @param to      收件地址
	 * @param title   邮件标题
	 * @param content 邮件内容
	 * @author MUYUA
	 */
	public void sendSimpleMail(String to, String title, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		// 发送人地址
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(title);
		message.setText(content);

		mailSender.send(message);
//		log.debug("【EmailUtil】邮件成功发送到>>{}", to);
	}

	/**
	 * 发送附件邮箱
	 *
	 * @param to       收件地址
	 * @param title    邮件标题
	 * @param content  邮件内容
	 * @param fileList 附件
	 */
	public void sendAttachmentsMail(String to, String title, String content, List<File> fileList) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(title);
			helper.setText(content);

			String fileName = null;
			for (File file : fileList) {
				fileName = MimeUtility.encodeText(file.getName(), "GB2312", "B");
				helper.addAttachment(fileName, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mailSender.send(message);
//		log.debug("【EmailUtil】附件邮件成功发送到>>{}", to);
	}

	/**
	 * 发送HTML邮箱
	 *
	 * @param to          收件地址
	 * @param title       邮件标题
	 * @param htmlContent 邮件内容
	 */
	public void sendHtmlMail(String to, String title, String htmlContent) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(title);
			helper.setText(htmlContent, true);

		} catch (MessagingException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
	}

}
