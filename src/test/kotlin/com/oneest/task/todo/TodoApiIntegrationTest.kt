package com.oneest.task.todo

import com.fasterxml.jackson.databind.ObjectMapper
import com.oneest.task.todo.dao.TodoRepository
import com.oneest.task.todo.model.Tag
import com.oneest.task.todo.model.Todo
import com.oneest.task.todo.testUtils.*
import com.oneest.task.todo.utils.*
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
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
class TodoApiIntegrationTest {

    lateinit var mvc: MockMvc

    lateinit var JWT: String

    lateinit var EXPIRED_JWT: String

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var todoRepository: TodoRepository


    @Before
    fun setup() {
        JWT = TestUtils.createJWT(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME)
        EXPIRED_JWT = TestUtils.createJWT(System.currentTimeMillis())
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(springSecurity())
                .build()
    }

    @After
    fun resetDb() {
        todoRepository.deleteAll()
    }

    @Test
    fun createATodo() {
        val todo = saveTodo(FIRST_TODO_TITLE, FIRST_TODO_TEXT, FIRST_TODO_TAG)

        val todos = todoRepository.findAll().toList()

        assertThat(todos.size == 1)
        assertThat(todos).extracting("title").containsOnly(FIRST_TODO_TITLE)
        assertThat(todos).extracting("text").containsOnly(FIRST_TODO_TEXT)
        assertThat(todos).extracting("tag").containsOnly(FIRST_TODO_TAG)
        assertThat(todo.id == 1L)
        assertThat(todo.creationDate != null)
        assertThat(todo.editDate != null)
    }

    @Test
    fun searchTodoByTitle() {
        addTodos()

        mvc.perform(MockMvcRequestBuilders.get("/todo?title=" + FIRST_TODO_TITLE).contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", CoreMatchers.`is`(FIRST_TODO_TITLE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", CoreMatchers.`is`(FIRST_TODO_TEXT)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tag", CoreMatchers.`is`(FIRST_TODO_TAG.toString())))
    }

    @Test
    fun searchTodoByText() {
        addTodos()

        mvc.perform(MockMvcRequestBuilders.get("/todo?text=" + SECOND_TODO_TEXT).contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", CoreMatchers.`is`(SECOND_TODO_TITLE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", CoreMatchers.`is`(SECOND_TODO_TEXT)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tag", CoreMatchers.`is`(SECOND_TODO_TAG.toString())))
    }

    @Test
    fun searchTodoByTitleAndTextAndTag() {
        addTodos()

        val url = "/todo?title=" + SECOND_TODO_TITLE + "&text=" + SECOND_TODO_TEXT + "&tag=" + SECOND_TODO_TAG

        mvc.perform(MockMvcRequestBuilders.get(url).contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", CoreMatchers.`is`(SECOND_TODO_TITLE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", CoreMatchers.`is`(SECOND_TODO_TEXT)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tag", CoreMatchers.`is`(SECOND_TODO_TAG.toString())))
    }

    @Test
    fun searchTodoByTag() {
        addTodos()

        mvc.perform(MockMvcRequestBuilders.get("/todo?tag=" + FIRST_TODO_TAG).contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", CoreMatchers.`is`(FIRST_TODO_TITLE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", CoreMatchers.`is`(FIRST_TODO_TEXT)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tag", CoreMatchers.`is`(FIRST_TODO_TAG.toString())))
    }

    @Test
    fun getAllTodos() {
        addTodos()

        mvc.perform(MockMvcRequestBuilders.get("/todo").contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", CoreMatchers.`is`(FIRST_TODO_TITLE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", CoreMatchers.`is`(FIRST_TODO_TEXT)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tag", CoreMatchers.`is`(FIRST_TODO_TAG.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", CoreMatchers.`is`(SECOND_TODO_TITLE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].text", CoreMatchers.`is`(SECOND_TODO_TEXT)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].tag", CoreMatchers.`is`(SECOND_TODO_TAG.toString())))
    }

    @Test
    fun removeTodo() {
        val todo = saveTodo(FIRST_TODO_TITLE, FIRST_TODO_TEXT, FIRST_TODO_TAG)

        mvc.perform(MockMvcRequestBuilders.delete("/todo/" + todo.id).contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isOk)

        val todos = todoRepository.findAll().toList()

        assertThat(todos.isEmpty())
    }

    @Test
    fun createTodoWithoutAuthentication() {
        val objectMapper = ObjectMapper()
        val todo = Todo()
        todo.title = FIRST_TODO_TITLE
        todo.text = FIRST_TODO_TEXT
        todo.tag = FIRST_TODO_TAG

        mvc.perform(MockMvcRequestBuilders.post("/todo").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(todo)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage", CoreMatchers.`is`(AUTH_FAILED_MESSAGE)))
    }

    @Test
    fun createTodoWithExpiredJWT() {
        val objectMapper = ObjectMapper()
        val todo = Todo()
        todo.title = FIRST_TODO_TITLE
        todo.text = FIRST_TODO_TEXT
        todo.tag = FIRST_TODO_TAG

        mvc.perform(MockMvcRequestBuilders.post("/todo").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(todo)).header(AUTH_HEADER, TOKEN_PREFIX + EXPIRED_JWT))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage", CoreMatchers.`is`(AUTH_FAILED_MESSAGE)))
    }

    @Test
    fun getTodosWithoutAuthentication() {
        mvc.perform(MockMvcRequestBuilders.get("/todo").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage", CoreMatchers.`is`(AUTH_FAILED_MESSAGE)))
    }

    @Test
    fun getTodosWithExpiredJWT() {
        mvc.perform(MockMvcRequestBuilders.get("/todo").contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + EXPIRED_JWT))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage", CoreMatchers.`is`(AUTH_FAILED_MESSAGE)))
    }

    @Test
    fun removoTodoWithoutAuthentication() {
        val todo = saveTodo(FIRST_TODO_TITLE, FIRST_TODO_TEXT, FIRST_TODO_TAG)

        mvc.perform(MockMvcRequestBuilders.delete("/todo/" + todo.id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage", CoreMatchers.`is`(AUTH_FAILED_MESSAGE)))
    }

    @Test
    fun removoTodoWithExpiredJWT() {
        val todo = saveTodo(FIRST_TODO_TITLE, FIRST_TODO_TEXT, FIRST_TODO_TAG)

        mvc.perform(MockMvcRequestBuilders.delete("/todo/" + todo.id).contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + EXPIRED_JWT))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage", CoreMatchers.`is`(AUTH_FAILED_MESSAGE)))
    }

    @Test
    fun removoTodoNotFound() {
        val idToRemove = 1
        mvc.perform(MockMvcRequestBuilders.delete("/todo/" + idToRemove).contentType(MediaType.APPLICATION_JSON).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("errorMessage", CoreMatchers.`is`(NO_TODO_FOUND_MESSAGE_WITH_ID + idToRemove)))
    }

    private fun saveTodo(title: String, text: String, tag: Tag): Todo {
        val objectMapper = ObjectMapper()
        val todo = Todo()
        todo.title = title
        todo.text = text
        todo.tag = tag

        mvc.perform(MockMvcRequestBuilders.post("/todo").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(todo)).header(AUTH_HEADER, TOKEN_PREFIX + JWT))
                .andExpect(MockMvcResultMatchers.status().isCreated)

        return todoRepository.findByTitle(todo.title!!)
    }

    private fun addTodos() {
        saveTodo(FIRST_TODO_TITLE, FIRST_TODO_TEXT, FIRST_TODO_TAG)
        saveTodo(SECOND_TODO_TITLE, SECOND_TODO_TEXT, SECOND_TODO_TAG)
    }
}