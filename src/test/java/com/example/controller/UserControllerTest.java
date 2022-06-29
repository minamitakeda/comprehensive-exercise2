package com.example.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import com.example.util.SessionUtil;
import com.example.util.XlsDataSetLoader;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@DbUnitConfiguration(dataSetLoader = XlsDataSetLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, // このテストクラスでDIを使えるように指定
		TransactionDbUnitTestExecutionListener.class // @DatabaseSetupや@ExpectedDatabaseなどを使えるように指定
})
class UserControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@DisplayName("メールアドレス入力画面の表示(l29-l31)")
	void test1() throws Exception {
		mockMvc.perform(get("/user")).andExpect(view().name("mail")).andReturn();
	}

	@Test
	@DisplayName("メールアドレス空欄で送信(入力値チェック)のため、メールアドレス入力画面に遷移(l49-52)")
	void test2() throws Exception {
		mockMvc.perform(get("/user/register").param("email", "　")).andExpect(view().name("mail")).andReturn();
	}

	@Test
	@DisplayName("メールアドレス一致かつ24時間以内にURL発行済みのため、メールアドレス入力画面に遷移(l55-59)")
	@DatabaseSetup(value = "classpath:test3.xlsx")
	@Transactional
	void test3() throws Exception {
		MvcResult mvcResult = mockMvc.perform(post("/user/register").param("email", "abc@gmail.com"))
				.andExpect(view().name("mail")).andExpect(model().hasErrors())
				.andExpect(model().attributeHasErrors("mailForm")).andReturn();

		BindingResult bindingResult = (BindingResult) mvcResult.getModelAndView().getModel()
				.get(BindingResult.MODEL_KEY_PREFIX + "mailForm");
		String message = bindingResult.getFieldError().getDefaultMessage();
		assertEquals("24時間以内にURLが発行されています", message);
	}

	@Test
	@DisplayName("既に会員登録があるため、送信完了画面に遷移(l60-63")
	@DatabaseSetup(value = "classpath:test4.xlsx")
	@Transactional
	void test4() throws Exception {
		mockMvc.perform(post("/user/register").param("email", "abc@gmail.com")).andExpect(view().name("sendCompletely"))
				.andReturn();
	}

	@Test
	@DisplayName("mailsテーブルに情報挿入(l64-75 insertまで)")
	@DatabaseSetup(value = "classpath:test5_1.xlsx")
	@ExpectedDatabase(value = "classpath:test5_2.xlsx", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@Transactional
	void test5() throws Exception {
		mockMvc.perform(post("/user/register").param("email", "abc@gmail.com"))
				.andExpect(view().name("redirect:/user/index2"));
	}

	@Test
	@DisplayName("メール送信完了画面の表示(l79-82)")
	void test6() throws Exception {
		mockMvc.perform(post("/user/index2")).andExpect(view().name("sendCompletely")).andReturn();
	}

	@Test
	@DisplayName("keyが一致かつ24時間以内にURL押下したため、ユーザー登録画面に遷移(l84-91)")
	@DatabaseSetup(value = "classpath:test7.xlsx")
	@Transactional
	void test7() throws Exception {
		mockMvc.perform(post("/user/sendmail").param("key", "0ba7c767-50d5-4506-9f62-79e52aa21426"))
				.andExpect(view().name("user")).andReturn();
	}

	@Test
	@DisplayName("keyは一致しているが、24時間以降にURL押下したため、URL無効画面に遷移(l84-93)")
	@DatabaseSetup(value = "classpath:test8.xlsx")
	@Transactional
	void test8() throws Exception {
		mockMvc.perform(post("/user/sendmail").param("key", "0ba7c767-50d5-4506-9f62-79e52aa21426"))
				.andExpect(view().name("InvalidUrl")).andReturn();
	}

	@Test
	@DisplayName("24時間以内にURLを押下したが、keyが一致していないため、URL無効画面に遷移(l84-90)")
	@DatabaseSetup(value = "classpath:test9.xlsx")
	@Transactional
	void test9() throws Exception {
		mockMvc.perform(post("/user/sendmail").param("key", "1ba7c767-50d5-4506-9f62-79e52aa21426"))
				.andExpect(view().name("InvalidUrl")).andReturn();
	}

	@Test
	@DisplayName("keyも一致せず、24時間以降にURL押下したため、URL無効画面遷移(l84-87,92-93)")
	@DatabaseSetup(value = "classpath:test10.xlsx")
	@Transactional
	void test10() throws Exception {
		mockMvc.perform(post("/user/sendmail").param("key", "1ba7c767-50d5-4506-9f62-79e52aa21426"))
				.andExpect(view().name("InvalidUrl")).andReturn();
	}

	@Test
	@DisplayName("パスワードと確認用パスワードが一致していないため、ユーザー登録不可(l95-102)")
	@DatabaseSetup(value = "classpath:test11_1.xlsx")
	@ExpectedDatabase(value = "classpath:test11_2.xlsx", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@Transactional
	void test11() throws Exception {
		MockHttpSession mockHtttpSession2 = SessionUtil.createMockHttpSession2();
		MvcResult mvcResult = mockMvc
				.perform(post("/user/userRegisterConfirm").session(mockHtttpSession2).param("name", "テスト")
						.param("ruby", "テスト").param("email", "abc@gmail.com").param("zipcode", "111-1111")
						.param("address", "住所").param("telephone", "090-1234-5678").param("password", "12345678")
						.param("confirmpassword", "87654321"))
				.andExpect(view().name("user")).andExpect(model().hasErrors())
				.andExpect(model().attributeHasErrors("userRegisterForm")).andReturn();

		BindingResult bindingResut = (BindingResult) mvcResult.getModelAndView().getModel()
				.get(BindingResult.MODEL_KEY_PREFIX + "userRegisterForm");
		String message = bindingResut.getFieldError().getDefaultMessage();
		assertEquals("パスワードが一致していません", message);
	}

	@Test
	@DisplayName("入力欄空欄で送信(入力値チェック)のため、ユーザー登録画面に遷移(l104-106)")
	void test12() throws Exception {
		mockMvc.perform(post("/user/userRegisterConfirm").param("name", "　").param("ruby", "　").param("email", "　")
				.param("zipcode", "　").param("address", "　").param("telephone", "　").param("password", "　")
				.param("confirmpassword", "　")).andExpect(view().name("user")).andReturn();
	}

	@Test
	@DisplayName("user_registersテーブルに情報挿入(l95-114)")
	@DatabaseSetup(value = "classpath:test13_1.xlsx")
	@ExpectedDatabase(value = "classpath:test13_2.xlsx", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@Transactional
	void test13() throws Exception {
		MockHttpSession mockHttpsession = SessionUtil.createMockHttpSession1();
		mockMvc.perform(post("/user/userRegisterConfirm").session(mockHttpsession)
				.param("key", "0ba7c767-50d5-4506-9f62-79e52aa21426").param("name", "テスト").param("ruby", "てすと")
				.param("email", "abc@gmail.com").param("zipcode", "111-1111").param("address", "住所")
				.param("telephone", "090-1234-5678").param("password", "12345678").param("confirmpassword", "12345678"))
				.andExpect(view().name("redirect:/user/index3")).andReturn();
	}

	@Test
	@DisplayName("mailsテーブルのststusをfalse→trueへ(l95-114)")
	@DatabaseSetup(value = "classpath:test14_1_1.xlsx")
	@ExpectedDatabase(value = "classpath:test14_2.xlsx", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@Transactional
	void test14() throws Exception {
		MockHttpSession mockHttpsession = SessionUtil.createMockHttpSession1();
		mockMvc.perform(post("/user/userRegisterConfirm").session(mockHttpsession).param("name", "テスト")
				.param("ruby", "てすと").param("email", "abc@gmail.com").param("zipcode", "111-1111")
				.param("address", "住所").param("telephone", "090-1234-5678").param("password", "12345678")
				.param("confirmpassword", "12345678")).andExpect(view().name("redirect:/user/index3")).andReturn();
	}

	@Test
	@DisplayName("ユーザー登録完了画面の表示(l116-119)")
	void test15() throws Exception {
		mockMvc.perform(post("/user/index3")).andExpect(view().name("registrationCompletely")).andReturn();
	}
}
