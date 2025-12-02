package com.bookstore.backend.service;

import com.bookstore.backend.dto.UserDTO;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.AddressRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Pageable pageable;
    private User admin;
    private User customer;

    @BeforeEach
    void setup() {
        pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        admin = User.builder()
                .id("u1")
                .email("admin@gmail.com")
                .fullname("Admin")
                .role(1)
                .status(1)
                .build();

        customer = User.builder()
                .id("u2")
                .email("customer@gmail.com")
                .fullname("Customer")
                .role(3)
                .status(1)
                .build();
    }

    // Lấy các khách hàng
    @Test
    void testGetCustomers_NoFilters() {
        Page<User> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(userRepository.findByRoleIn(List.of(3), pageable)).thenReturn(page);

        var result = userService.getAllCustomers(1, 10, null, null);
        assertEquals(1, result.getTotalElements());
        assertEquals("customer@gmail.com", result.getContent().get(0).getEmail());
    }

    @Test
    void testGetCustomers_FilterByEmail() {
        Page<User> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(userRepository.findByEmailContainingIgnoreCaseAndRoleIn(
                "cus", List.of(3), pageable)).thenReturn(page);

        var result = userService.getAllCustomers(1, 10, "cus", null);
        assertEquals("customer@gmail.com", result.getContent().get(0).getEmail());
    }

    // aLấy các quản trị viên
    @Test
    void testGetAdmins_NoFilters() {
        Page<User> page = new PageImpl<>(List.of(admin), pageable, 1);
        when(userRepository.findByRoleIn(List.of(0, 1, 2), pageable))
                .thenReturn(page);

        var result = userService.getAllAdmins(1, 10, null, null);
        assertEquals(1, result.getTotalElements());
    }

    // Lấy người dùng theo id
    void testGetUserById_Found() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(admin));

        var dto = userService.getUserById("u1");
        assertTrue(dto.isPresent());
        assertEquals("admin@gmail.com", dto.get().getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        var dto = userService.getUserById("u1");
        assertTrue(dto.isEmpty());
    }

    // Thêm người dùng
    @Test
    void testCreateUser_Success() {

        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("ENCODED");

        UserDTO dto = UserDTO.builder()
                .email("new@gmail.com")
                .fullname("New User")
                .password("123456")
                .status(1)
                .role(3)
                .build();

        when(userRepository.save(any())).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId("u99");
            return saved;
        });

        UserDTO result = userService.createUser(dto);

        assertNotNull(result.getId());
        assertNull(result.getPassword());
    }

    // Cập nhật người dùng
    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(admin));
        when(userRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("654321")).thenReturn("ENCODED");

        UserDTO dto = UserDTO.builder()
                .email("new@gmail.com")
                .fullname("New Admin")
                .password("654321")
                .role(2)
                .status(0)
                .build();

        UserDTO result = userService.updateUser("u1", dto);

        assertEquals("new@gmail.com", result.getEmail());
        assertNull(result.getPassword());
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());
        var result = userService.updateUser("u1", new UserDTO());
        assertNull(result);
    }

    // Cập nhật status người dùng
    @Test
    void testUpdateUserStatus_Success() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(admin));
        when(userRepository.save(admin)).thenReturn(admin);

        var result = userService.updateUserStatus("u1", 0);

        assertEquals(0, result.getStatus());
    }

    @Test
    void testUpdateUserStatus_NotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserStatus("u1", 0));
    }

    // Xóa người dùng
    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(admin));
        when(orderRepository.existsByUser(admin)).thenReturn(false);

        assertDoesNotThrow(() -> userService.deleteUser("u1"));

        verify(addressRepository).deleteByUserId("u1");
        verify(cartRepository).deleteByUserId("u1");
        verify(userRepository).deleteById("u1");
    }

    @Test
    void testDeleteUser_HasOrders() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(admin));
        when(orderRepository.existsByUser(admin)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> userService.deleteUser("u1"));
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser("u1"));
    }
}
