package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.domain.Mail;
import com.example.domain.UserRegister;

@Repository
public class UserRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

//	@Autowired
//	private PasswordEncoder passwordEncoder;

	private static final RowMapper<Mail> USERREGISTER_ROW_MAPPER = (rs, i) -> {
		Mail userRegister = new Mail();
		userRegister.setId(rs.getInt("id"));
		userRegister.setEmail(rs.getString("email"));
		userRegister.setKey(rs.getString("key"));
		userRegister.setDate(rs.getTimestamp("date"));
		userRegister.setStatus(rs.getBoolean("status"));
		return userRegister;
	};

	private static final RowMapper<UserRegister> USER_ROW_MAPPER = (rs, i) -> {
		UserRegister user = new UserRegister();
		user.setName(rs.getString("name"));
		user.setRuby(rs.getString("ruby"));
		user.setEmail(rs.getString("email"));
		user.setZipcode(rs.getString("zipcode"));
		user.setAddress(rs.getString("address"));
		user.setTelephone(rs.getString("telephone"));
		user.setPassword(rs.getString("password"));
		user.setRegistDate(rs.getTimestamp("regist_date"));
		user.setRegistUser(rs.getInt("regist_user"));
		user.setUpdateDate(rs.getTimestamp("update_date"));
		user.setUpdateUser(rs.getInt("update_user"));
		user.setStatus(rs.getBoolean("status"));
		return user;
	};

	/**
	 * @param メールアドレス(user_rgistersテーブル)
	 * @return ユーザー情報(存在しない場合はnullを返す)
	 */
	public List<Mail> findByEmail(String email) {
		String sql = "SELECT * FROM mails WHERE email=:email AND date +cast('1 days' as interval)>now();";
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
		List<Mail> userRegisterList = template.query(sql, param, USERREGISTER_ROW_MAPPER);
		if (userRegisterList.size() == 0) {
			return null;
		}
		System.out.println(userRegisterList);
		return userRegisterList;
	}

	/**
	 * @param メールアドレス(usersテーブル)
	 * @return ユーザー情報(存在しない場合nullを返す)
	 */
	public List<UserRegister> findByUser(String email) {
		String sql = "SELECT * FROM user_registers WHERE email=:email";
		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);
		List<UserRegister> userList = template.query(sql, param, USER_ROW_MAPPER);
		if (userList.size() == 0) {
			return null;
		}
		return userList;
	}

	/*
	 * 
	 * ユーザー情報を挿入
	 * 
	 * @param user
	 */
	public void insertUser(UserRegister userRegister) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		userRegister.setPassword(passwordEncoder.encode(userRegister.getPassword()));
		SqlParameterSource param = new BeanPropertySqlParameterSource(userRegister);
		String sql = "INSERT INTO user_registers(name,ruby,email,zipcode,address,telephone,password)"
				+ " VALUES(:name,:ruby,:email,:zipcode,:address,:telephone,:password)";
		template.update(sql, param);
	}

	public void insertMail(Mail mail) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(mail);
		String sql = "INSERT INTO mails(email,key) VALUES(:email,:key)";
		template.update(sql, param);
	}

	public List<Mail> findByEmail2(String key) {
		String sql = "SELECT * FROM mails WHERE key=:key AND date +cast('1 days' as interval)>now(); AND status=false"; 
		SqlParameterSource param = new MapSqlParameterSource().addValue("key", key);
		List<Mail> userRegisterList3 = template.query(sql, param, USERREGISTER_ROW_MAPPER);
		if (userRegisterList3.size() == 0) {
			return null;
		}
		System.out.println(userRegisterList3);
		return userRegisterList3;
	}

	public void updateStatus(String key) {
		String sql = "UPDATE mails SET status=true where key=:key";
		SqlParameterSource param = new MapSqlParameterSource().addValue("key", key);
		template.update(sql, param);
	}

}
