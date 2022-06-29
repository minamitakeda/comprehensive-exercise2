package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.Mail;
import com.example.domain.UserRegister;
import com.example.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * メールアドレスからユーザー情報取得(user_registersテーブル)
	 * 
	 * @param メールアドレス
	 * @return ユーザー情報
	 */
	public List<Mail> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * メールアドレスからユーザー情報取得(usersテーブル)
	 */
	public List<UserRegister> findByUser(String email) {
		return userRepository.findByUser(email);
	}

	public void insertMail(Mail mail) {
		userRepository.insertMail(mail);
	}

	public void insertUser(UserRegister userRegister) {
		userRepository.insertUser(userRegister);
	}

	public List<Mail> findByEmail2(String key) {
		return userRepository.findByEmail2(key);
	}

	public void updateStatus(String key) {
		userRepository.updateStatus(key);
	}
}
