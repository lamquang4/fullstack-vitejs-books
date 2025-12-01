package com.bookstore.backend.controller;

import com.bookstore.backend.dto.AddressDTO;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private AddressDTO dto;
    private Address address;

    private final String USER_ID = "user123";
    private final String ADDRESS_ID = "addr1";

    @BeforeEach
    void setUp() {

        dto = AddressDTO.builder()
                .id(ADDRESS_ID)
                .fullname("Nguyen Van A")
                .phone("0123456789")
                .speaddress("123 Le Loi")
                .ward("Ben Thanh")
                .city("HCM")
                .userId(USER_ID)
                .build();

        address = new Address();
        address.setId(ADDRESS_ID);
    }

    // Lấy các địa chỉ của người dùng
    @Test
    @WithMockUser
    void getAddressesByUserId_shouldReturnList() throws Exception {

        Mockito.when(addressService.getAddressesByUserId(USER_ID))
                .thenReturn(List.of(address));

        mockMvc.perform(get("/api/address/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ADDRESS_ID));
    }

    // Lấy địa chỉ theo id
    @Test
    @WithMockUser
    void getAddressByIdAndUserId_shouldReturnAddress() throws Exception {

        Mockito.when(addressService.getAddressByIdAndUserId(ADDRESS_ID, USER_ID))
                .thenReturn(address);

        mockMvc.perform(get("/api/address/{userId}/{id}", USER_ID, ADDRESS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ADDRESS_ID));
    }

    // Thêm địa chỉ
    @Test
    @WithMockUser
    void createAddress_shouldReturnCreatedAddress() throws Exception {

        Mockito.when(addressService.createAddress(Mockito.any(AddressDTO.class)))
                .thenReturn(address);

        mockMvc.perform(post("/api/address")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ADDRESS_ID));
    }

    // Cập nhật địa chỉ
    @Test
    @WithMockUser
    void updateAddress_shouldReturnUpdatedAddress() throws Exception {

        Mockito.when(addressService.updateAddress(Mockito.eq(ADDRESS_ID), Mockito.any(AddressDTO.class)))
                .thenReturn(address);

        mockMvc.perform(put("/api/address/{id}", ADDRESS_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ADDRESS_ID));
    }

    // Xóa địa chỉ
    @Test
    @WithMockUser
    void deleteAddress_shouldReturnNoContent() throws Exception {

        Mockito.doNothing().when(addressService).deleteAddress(ADDRESS_ID);

        mockMvc.perform(delete("/api/address/{id}", ADDRESS_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
