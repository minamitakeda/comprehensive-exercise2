package com.example.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpSession;

public class SessionUtil {

	private static MockHttpSession createMockHttpSession(Map<String, Object> sessions) {
		MockHttpSession mockHttpSession = new MockHttpSession();
		for (Map.Entry<String, Object> session : sessions.entrySet()) {
			mockHttpSession.setAttribute(session.getKey(), session.getValue());
		}
		return mockHttpSession;
	}

	public static MockHttpSession createMockHttpSession1() {
		Map<String, Object> sessionMap1 = new LinkedHashMap<String, Object>();
		sessionMap1.put("key", "0ba7c767-50d5-4506-9f62-79e52aa21426");
		return createMockHttpSession(sessionMap1);
	}

}
