package io.github.pmleczek.javabookstoreproject.controller;

import io.github.pmleczek.javabookstoreproject.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseIntegrationTest {

    @Test
    void register_returnsCreatedWithToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "alice", "password": "secret123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void authenticate_withValidCredentials_returnsToken() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "bob", "password": "secret123"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login": "bob", "password": "secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void register_withBlankUsername_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "", "password": "secret123"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
