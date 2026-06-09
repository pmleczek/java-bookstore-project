package io.github.pmleczek.javabookstoreproject.integration;

import io.github.pmleczek.javabookstoreproject.repository.BookRepository;
import io.github.pmleczek.javabookstoreproject.repository.ReservationRepository;
import io.github.pmleczek.javabookstoreproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
public abstract class BaseIntegrationTest {
    @Autowired protected MockMvc mockMvc;
    @Autowired protected JsonMapper objectMapper;

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        reservationRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }
}
