package io.github.pmleczek.javabookstoreproject.controller;

import io.github.pmleczek.javabookstoreproject.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminControllerTest extends BaseIntegrationTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllReservations_withAdminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/api/admin/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserReservations_withAdminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/api/admin/reservations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllReservations_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/reservations"))
                .andExpect(status().isForbidden());
    }
}
