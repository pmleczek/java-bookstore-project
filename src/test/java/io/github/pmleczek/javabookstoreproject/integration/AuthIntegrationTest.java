package io.github.pmleczek.javabookstoreproject.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /auth/register creates user and returns JWT")
    void register_returnsToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "newuser", "password": "password123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("POST /auth/auth with valid credentials returns JWT")
    void authenticate_withValidCredentials_returnsToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "loginuser", "password": "password123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "loginuser", "password": "password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    @DisplayName("GET /api/books with valid JWT succeeds (exercises JwtAuthFilter auth path)")
    void requestWithValidJwt_isAuthenticated() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "jwtuser", "password": "password123"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").textValue();
        assertThat(token).isNotBlank();

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /auth/register with blank username returns 400")
    void register_withBlankUsername_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "", "password": "password123"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
