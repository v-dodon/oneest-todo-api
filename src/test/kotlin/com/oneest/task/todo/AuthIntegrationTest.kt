package com.oneest.task.todo

import com.fasterxml.jackson.databind.ObjectMapper
import com.oneest.task.todo.dao.UserRepository
import com.oneest.task.todo.model.TodoUser
import com.oneest.task.todo.testUtils.PASSWORD
import com.oneest.task.todo.testUtils.USERNAME
import com.oneest.task.todo.utils.AUTH_HEADER
import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment =
SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = arrayOf(TodoApplication::class))
@TestPropertySource(
        locations = arrayOf("classpath:application-integrationtest.properties"))
class AuthIntegrationTest {
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var userRepository: UserRepository


    @Before
    fun setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .build()
    }

    @After
    fun resetDb() {
        userRepository.deleteAll()
    }


    @Test
    fun registerUser() {

        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getUserAsJson()))
                .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun loginUser() {
        registerUser()

        val mvcResult = mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getUserAsJson()))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        val header = mvcResult.response.getHeader(AUTH_HEADER)

        assertThat(StringUtils.isNotEmpty(header))
    }

    @Test
    fun loginUserNotRegistered() {
        val mvcResult = mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getUserAsJson()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andReturn()

        val header = mvcResult.response.getHeader(AUTH_HEADER)

        assertThat(StringUtils.isEmpty(header))
    }

    fun getUserAsJson(): String {
        val mapper = ObjectMapper()
        val user = TodoUser()
        user.username = USERNAME
        user.password = PASSWORD

        return mapper.writeValueAsString(user)
    }
}