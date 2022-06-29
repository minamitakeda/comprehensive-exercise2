package com.example.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserRegisterForm {
	/** 名前 */
	@NotBlank(message = "名前を入力してください")
	private String name;
	/** ふりがな */
	@NotBlank(message = "ふりがなを入力してください")
	private String ruby;
	/** 郵便番号 */
	@NotBlank(message = "郵便番号を入力してください")
	private String zipcode;
	/** 住所 */
	@NotBlank(message = "住所を入力してください")
	private String address;
	/** 電話番号 */
	@NotBlank(message = "電話番号を入力してください")
	private String telephone;
	/** パスワード */
	@NotBlank(message = "パスワードを入力してください")
	@Size(min = 8, max = 16, message = "パスワードは8文字以上16文字以内で入力してください")
	private String password;
	/** 確認パスワード */
	@NotBlank(message = "確認用パスワードを入力してください")
	private String confirmpassword;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRuby() {
		return ruby;
	}

	public void setRuby(String ruby) {
		this.ruby = ruby;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmpassword() {
		return confirmpassword;
	}

	public void setConfirmpassword(String confirmpassword) {
		this.confirmpassword = confirmpassword;
	}

}
