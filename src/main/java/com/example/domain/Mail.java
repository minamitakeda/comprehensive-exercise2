package com.example.domain;

import java.sql.Timestamp;

public class Mail {
	/** id(主キー) */
	private Integer id;
	/** メールアドレス */
	private String email;
	/** ランダムURL */
	private String key;
	/** URL発行日時 */
	private Timestamp date;
	/** 削除フラグ */
	private boolean status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Mail [id=" + id + ", email=" + email + ", key=" + key + ", date=" + date + ", status=" + status + "]";
	}

}
