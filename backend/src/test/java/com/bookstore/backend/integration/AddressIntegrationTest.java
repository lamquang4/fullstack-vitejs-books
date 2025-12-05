package com.bookstore.backend.integration;

import com.bookstore.backend.dto.AddressDTO;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.AddressRepository;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.ValidationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AddressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setup() {
        addressRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .fullname("Nguyen Van A")
                .email("user@example.com")
                .password("123456")
                .role(3)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    // --------------------
    // CREATE
    // --------------------
    @Test
    void testCreateAddress_Success() throws Exception {

        AddressDTO dto = AddressDTO.builder()
                .fullname("Nguyen Van B")
                .phone("0909000001")
                .speaddress("123 ABC")
                .ward("Ward 1")
                .city("HCM")
                .userId(user.getId())
                .build();

        // mock validation
        try (MockedStatic<ValidationUtils> util = org.mockito.Mockito.mockStatic(ValidationUtils.class)) {
            util.when(() -> ValidationUtils.validatePhone(dto.getPhone())).thenReturn(true);

            mockMvc.perform(post("/api/address")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullname").value("Nguyen Van B"));

            assertEquals(1, addressRepository.count());
        }
    }

    // --------------------
    // GET LIST
    // --------------------
    @Test
    void testGetAddressesByUserId() throws Exception {

        Address address = Address.builder()
                .fullname("Test")
                .phone("0123456789")
                .speaddress("Test Street")
                .ward("Ward 1")
                .city("HN")
                .user(user)
                .build();

        addressRepository.save(address);

        mockMvc.perform(get("/api/address/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].city").value("HN"));
    }

    // --------------------
    // GET ONE
    // --------------------
    @Test
    void testGetAddressByIdAndUserId() throws Exception {

        Address address = Address.builder()
                .fullname("Test User")
                .phone("0123456789")
                .speaddress("Test Street")
                .ward("Ward 1")
                .city("DN")
                .user(user)
                .build();

        addressRepository.save(address);

        mockMvc.perform(get("/api/address/{userId}/{id}",
                        user.getId(), address.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("DN"));
    }

    // --------------------
    // UPDATE
    // --------------------
    @Test
    void testUpdateAddress_Success() throws Exception {

        Address address = Address.builder()
                .fullname("Old Name")
                .phone("0123456789")
                .speaddress("Old Address")
                .ward("Old Ward")
                .city("Old City")
                .user(user)
                .build();

        addressRepository.save(address);

        AddressDTO update = AddressDTO.builder()
                .fullname("New Name")
                .phone("0999999999")
                .speaddress("New Address")
                .ward("New Ward")
                .city("New City")
                .userId(user.getId())
                .build();

        try (MockedStatic<ValidationUtils> util = org.mockito.Mockito.mockStatic(ValidationUtils.class)) {
            util.when(() -> ValidationUtils.validatePhone(update.getPhone())).thenReturn(true);

            mockMvc.perform(put("/api/address/{id}", address.getId())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(update)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullname").value("New Name"));
        }
    }

    // --------------------
    // DELETE
    // --------------------
    @Test
    void testDeleteAddress_Success() throws Exception {

        Address address = Address.builder()
                .fullname("Del User")
                .phone("0999888777")
                .speaddress("Del Street")
                .ward("Del Ward")
                .city("HCM")
                .user(user)
                .build();

        addressRepository.save(address);

        mockMvc.perform(delete("/api/address/{id}", address.getId()).with(csrf()))
                .andExpect(status().isNoContent());

        assertEquals(0, addressRepository.count());
    }
}
