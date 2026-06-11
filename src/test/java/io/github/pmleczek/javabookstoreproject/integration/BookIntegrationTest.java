package io.github.pmleczek.javabookstoreproject.integration;

import io.github.pmleczek.javabookstoreproject.entity.Book;
import io.github.pmleczek.javabookstoreproject.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @WithMockUser
    @DisplayName("GET /api/books returns all books")
    void getAll_returnsAllBooks() throws Exception {
        bookRepository.save(new Book(null, "Clean Code", "Robert Martin", 5));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Clean Code"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/books/{id} returns book by id")
    void getById_returnsBook() throws Exception {
        Book saved = bookRepository.save(new Book(null, "Effective Java", "Joshua Bloch", 3));

        mockMvc.perform(get("/api/books/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/books creates and persists book")
    void create_persistsBook() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "The Pragmatic Programmer", "author": "Dave Thomas", "quantity": 4}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("The Pragmatic Programmer"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        Long id = json.get("id").longValue();
        assertThat(bookRepository.findById(id)).isPresent();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/books/{id} deletes book")
    void delete_removesBook() throws Exception {
        Book saved = bookRepository.save(new Book(null, "To Delete", "Author", 1));

        mockMvc.perform(delete("/api/books/" + saved.getId()))
                .andExpect(status().isOk());

        assertThat(bookRepository.findById(saved.getId())).isEmpty();
    }
}
