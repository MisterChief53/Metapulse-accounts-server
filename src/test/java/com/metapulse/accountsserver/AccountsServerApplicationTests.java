package com.metapulse.accountsserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.HttpStatus;

@SpringBootTest
@AutoConfigureMockMvc
class AccountsServerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testHelloEndpoint() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Hello World!"));
	}

	@Test
	public void testRegisterUser() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
						.param("name", "John2") // Has to be a new User
						.param("password", "password123"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("User registered successfully"));
	}

	@Test
	void testVoidUsername() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
						.param("name", "") // Has to be a new User
						.param("password", ""))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized())
				.andExpect(MockMvcResultMatchers.content().string("Your username or password is empty"));
	}

	@Test
	void contextLoads() {
	}

}
