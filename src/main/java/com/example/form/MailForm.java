package com.example.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class MailForm {
	/** メールアドレス */
	@NotBlank(message = "メールアドレスを入力してください")
	@Email(message = "メールアドレスの形式が不正です")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
