package com.example.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.UserRegister;
import com.example.domain.Mail;
import com.example.form.UserRegisterForm;
import com.example.form.MailForm;
import com.example.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@RequestMapping("")
	public String index(MailForm mailForm, Model model) {
		return "mail";
	}

	@Autowired
	private MailSender mailSender;
	@Autowired
	private UserService userService;
	@Autowired
	private HttpSession session;

	@ModelAttribute
	public UserRegisterForm setUpForm() {
		return new UserRegisterForm();
	}

	/**
	 * 入力値チェック
	 */
	@RequestMapping("/register")
	public String register(@Validated MailForm mailForm, BindingResult rs, Model model) {
		if (rs.hasErrors()) {
			return "mail";
		}

		// メールアドレスがあるか確認
		List<Mail> userRegisterList = userService.findByEmail(mailForm.getEmail());
		if (userRegisterList != null) {
			rs.rejectValue("email", null, "24時間以内にURLが発行されています");
			return "mail";
		}
		List<UserRegister> userList = userService.findByUser(mailForm.getEmail());
		if (userList != null) {
			return "sendCompletely";
		} else {
			String key = UUID.randomUUID().toString();
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom("coffeeShopMaster2022@gmail.com");
			msg.setTo(mailForm.getEmail());
			msg.setSubject("ユーザー登録URLの送付");
			msg.setText("http://localhost:8080/user/sendmail?key=" + key);
			mailSender.send(msg);
			Mail mail = new Mail();
			mail.setEmail(mailForm.getEmail());
			mail.setKey(key);
			userService.insertMail(mail);
		}
		return "redirect:/user/index2";
	}

	@RequestMapping("/index2")
	public String index2() {
		return "sendCompletely";
	}

	@RequestMapping("/sendmail")
	public String index2(String key) {
		session.setAttribute("key", key);
		List<Mail> userRegisterList3 = userService.findByEmail2(key);
//		System.out.println(userRegisterList3);
		if (userRegisterList3 != null) {
			return "user";
		}
		return "InvalidUrl";
	}

	@RequestMapping("/userRegisterConfirm")
	public String userRegisterConfirm(@Validated UserRegisterForm userRegisterForm, BindingResult result) {
		String key = (String) session.getAttribute("key");
		System.out.println(userRegisterForm.getPassword());
		System.out.println(userRegisterForm.getConfirmpassword());
		if (!userRegisterForm.getPassword().equals(userRegisterForm.getConfirmpassword())) {
			result.rejectValue("confirmpassword", "", "パスワードが一致していません");
		}

		if (result.hasErrors()) {
			return "user";
		}
		List<Mail> userRegisterList = userService.findByEmail2(key);
		UserRegister userRegister = new UserRegister();
		BeanUtils.copyProperties(userRegisterForm, userRegister);
		userRegister.setEmail(userRegisterList.get(0).getEmail());
		userService.insertUser(userRegister);
		userService.updateStatus(key);
		return "redirect:/user/index3";
	}

	@RequestMapping("/index3")
	public String index3() {
		return "registrationCompletely";
	}
}
