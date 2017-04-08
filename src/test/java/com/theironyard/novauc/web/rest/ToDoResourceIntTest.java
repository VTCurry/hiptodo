package com.theironyard.novauc.web.rest;

import com.theironyard.novauc.HipToDoApp;

import com.theironyard.novauc.domain.ToDo;
import com.theironyard.novauc.repository.ToDoRepository;
import com.theironyard.novauc.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ToDoResource REST controller.
 *
 * @see ToDoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HipToDoApp.class)
public class ToDoResourceIntTest {

    private static final String DEFAULT_TODO_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TODO_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TODO_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_TODO_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restToDoMockMvc;

    private ToDo toDo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ToDoResource toDoResource = new ToDoResource(toDoRepository);
        this.restToDoMockMvc = MockMvcBuilders.standaloneSetup(toDoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ToDo createEntity(EntityManager em) {
        ToDo toDo = new ToDo()
            .todoName(DEFAULT_TODO_NAME)
            .todoDescription(DEFAULT_TODO_DESCRIPTION)
            .creationDate(DEFAULT_CREATION_DATE);
        return toDo;
    }

    @Before
    public void initTest() {
        toDo = createEntity(em);
    }

    @Test
    @Transactional
    public void createToDo() throws Exception {
        int databaseSizeBeforeCreate = toDoRepository.findAll().size();

        // Create the ToDo
        restToDoMockMvc.perform(post("/api/to-dos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toDo)))
            .andExpect(status().isCreated());

        // Validate the ToDo in the database
        List<ToDo> toDoList = toDoRepository.findAll();
        assertThat(toDoList).hasSize(databaseSizeBeforeCreate + 1);
        ToDo testToDo = toDoList.get(toDoList.size() - 1);
        assertThat(testToDo.getTodoName()).isEqualTo(DEFAULT_TODO_NAME);
        assertThat(testToDo.getTodoDescription()).isEqualTo(DEFAULT_TODO_DESCRIPTION);
        assertThat(testToDo.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    public void createToDoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = toDoRepository.findAll().size();

        // Create the ToDo with an existing ID
        toDo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restToDoMockMvc.perform(post("/api/to-dos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toDo)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ToDo> toDoList = toDoRepository.findAll();
        assertThat(toDoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllToDos() throws Exception {
        // Initialize the database
        toDoRepository.saveAndFlush(toDo);

        // Get all the toDoList
        restToDoMockMvc.perform(get("/api/to-dos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toDo.getId().intValue())))
            .andExpect(jsonPath("$.[*].todoName").value(hasItem(DEFAULT_TODO_NAME.toString())))
            .andExpect(jsonPath("$.[*].todoDescription").value(hasItem(DEFAULT_TODO_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    public void getToDo() throws Exception {
        // Initialize the database
        toDoRepository.saveAndFlush(toDo);

        // Get the toDo
        restToDoMockMvc.perform(get("/api/to-dos/{id}", toDo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(toDo.getId().intValue()))
            .andExpect(jsonPath("$.todoName").value(DEFAULT_TODO_NAME.toString()))
            .andExpect(jsonPath("$.todoDescription").value(DEFAULT_TODO_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingToDo() throws Exception {
        // Get the toDo
        restToDoMockMvc.perform(get("/api/to-dos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateToDo() throws Exception {
        // Initialize the database
        toDoRepository.saveAndFlush(toDo);
        int databaseSizeBeforeUpdate = toDoRepository.findAll().size();

        // Update the toDo
        ToDo updatedToDo = toDoRepository.findOne(toDo.getId());
        updatedToDo
            .todoName(UPDATED_TODO_NAME)
            .todoDescription(UPDATED_TODO_DESCRIPTION)
            .creationDate(UPDATED_CREATION_DATE);

        restToDoMockMvc.perform(put("/api/to-dos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedToDo)))
            .andExpect(status().isOk());

        // Validate the ToDo in the database
        List<ToDo> toDoList = toDoRepository.findAll();
        assertThat(toDoList).hasSize(databaseSizeBeforeUpdate);
        ToDo testToDo = toDoList.get(toDoList.size() - 1);
        assertThat(testToDo.getTodoName()).isEqualTo(UPDATED_TODO_NAME);
        assertThat(testToDo.getTodoDescription()).isEqualTo(UPDATED_TODO_DESCRIPTION);
        assertThat(testToDo.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingToDo() throws Exception {
        int databaseSizeBeforeUpdate = toDoRepository.findAll().size();

        // Create the ToDo

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restToDoMockMvc.perform(put("/api/to-dos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(toDo)))
            .andExpect(status().isCreated());

        // Validate the ToDo in the database
        List<ToDo> toDoList = toDoRepository.findAll();
        assertThat(toDoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteToDo() throws Exception {
        // Initialize the database
        toDoRepository.saveAndFlush(toDo);
        int databaseSizeBeforeDelete = toDoRepository.findAll().size();

        // Get the toDo
        restToDoMockMvc.perform(delete("/api/to-dos/{id}", toDo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ToDo> toDoList = toDoRepository.findAll();
        assertThat(toDoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ToDo.class);
    }
}
