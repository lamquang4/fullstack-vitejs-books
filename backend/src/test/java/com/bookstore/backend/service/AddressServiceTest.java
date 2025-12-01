package com.bookstore.backend.service;

import com.bookstore.backend.dto.AddressDTO;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.AddressRepository;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.ValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private AddressDTO dto;
    private User user;
    private Address address;

    @BeforeEach
    void setup() {
        dto = AddressDTO.builder()
                .id("addr1")
                .fullname("John Doe")
                .phone("0987654321")
                .speaddress("123 ABC")
                .ward("Ward 1")
                .city("Hanoi")
                .userId("user123")
                .build();

        user = User.builder()
                .id("user123")
                .fullname("John Doe")
                .email("john@example.com")
                .build();

        address = Address.builder()
                .id("addr1")
                .fullname("John Doe")
                .phone("0987654321")
                .speaddress("123 ABC")
                .ward("Ward 1")
                .city("Hanoi")
                .user(user)
                .build();
    }

    // Lấy danh sách địa chỉ của id người dùng
    @Test
    void testGetAddressesByUserId_Success() {
        when(addressRepository.findByUserId("user123"))
                .thenReturn(List.of(address));

        List<Address> list = addressService.getAddressesByUserId("user123");

        assertEquals(1, list.size());
        verify(addressRepository).findByUserId("user123");
    }

    // Lấy địa chỉ bằng id của id người dùng
    @Test
    void testGetAddressByIdAndUserId_Success() {
        when(addressRepository.findByIdAndUserId("addr1", "user123"))
                .thenReturn(Optional.of(address));

        Address result = addressService.getAddressByIdAndUserId("addr1", "user123");

        assertNotNull(result);
        assertEquals("addr1", result.getId());
    }

    @Test
    void testGetAddressByIdAndUserId_NotFound() {
        when(addressRepository.findByIdAndUserId("addr1", "user123"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> addressService.getAddressByIdAndUserId("addr1", "user123"));
    }

    // Tạo địa chỉ
    @Test
    void testCreateAddress_Success() {
        try (MockedStatic<ValidationUtils> utilities = mockStatic(ValidationUtils.class)) {

            utilities.when(() -> ValidationUtils.validatePhone(dto.getPhone()))
                    .thenReturn(true);

            when(userRepository.findById("user123")).thenReturn(Optional.of(user));
            when(addressRepository.save(any(Address.class))).thenReturn(address);

            Address result = addressService.createAddress(dto);

            assertEquals("addr1", result.getId());
            verify(addressRepository).save(any(Address.class));
        }
    }

    @Test
    void testCreateAddress_InvalidPhone() {
        try (MockedStatic<ValidationUtils> utilities = mockStatic(ValidationUtils.class)) {

            utilities.when(() -> ValidationUtils.validatePhone(dto.getPhone()))
                    .thenReturn(false);

            assertThrows(IllegalArgumentException.class,
                    () -> addressService.createAddress(dto));
        }
    }

    @Test
    void testCreateAddress_UserNotFound() {
        try (MockedStatic<ValidationUtils> utilities = mockStatic(ValidationUtils.class)) {

            utilities.when(() -> ValidationUtils.validatePhone(dto.getPhone()))
                    .thenReturn(true);

            when(userRepository.findById("user123")).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> addressService.createAddress(dto));
        }
    }

    // Cập nhật địa chỉ
    @Test
    void testUpdateAddress_Success() {
        AddressDTO updateDto = AddressDTO.builder()
                .fullname("Updated Name")
                .phone("0999999999")
                .speaddress("New Addr")
                .ward("New Ward")
                .city("New City")
                .userId("user123")
                .build();

        try (MockedStatic<ValidationUtils> utilities = mockStatic(ValidationUtils.class)) {

            utilities.when(() -> ValidationUtils.validatePhone(updateDto.getPhone()))
                    .thenReturn(true);

            when(addressRepository.findById("addr1")).thenReturn(Optional.of(address));
            when(userRepository.findById("user123")).thenReturn(Optional.of(user));
            when(addressRepository.save(address)).thenReturn(address);

            Address result = addressService.updateAddress("addr1", updateDto);

            assertEquals("Updated Name", result.getFullname());
            assertEquals("New City", result.getCity());
            verify(addressRepository).save(address);
        }
    }

    @Test
    void testUpdateAddress_NotFound() {
        try (MockedStatic<ValidationUtils> utilities = mockStatic(ValidationUtils.class)) {

            utilities.when(() -> ValidationUtils.validatePhone(dto.getPhone()))
                    .thenReturn(true);

            when(addressRepository.findById("missing")).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> addressService.updateAddress("missing", dto));
        }
    }

    // Xóa địa chỉ
    @Test
    void testDeleteAddress_Success() {
        when(addressRepository.existsById("addr1")).thenReturn(true);

        addressService.deleteAddress("addr1");

        verify(addressRepository).deleteById("addr1");
    }

    @Test
    void testDeleteAddress_NotFound() {
        when(addressRepository.existsById("missing")).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> addressService.deleteAddress("missing"));
    }
}
