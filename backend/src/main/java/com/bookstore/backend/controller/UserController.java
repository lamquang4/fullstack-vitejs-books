package com.bookstore.backend.controller;
import com.bookstore.backend.dto.AdminDTO;
import com.bookstore.backend.dto.CustomerDTO;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.service.UserService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // customer
@GetMapping("/customer")
public ResponseEntity<?> getAllCustomers(  @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "12") int limit,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer status) {
    Page<CustomerDTO> customers = userService.getAllCustomers(page, limit, q, status);
    return ResponseEntity.ok(Map.of(
        "customers", customers.getContent(),
        "totalPages", customers.getTotalPages(),
        "total", customers.getTotalElements()
    ));
}

    @GetMapping("/customer/{id}")
    public ResponseEntity<User> getCustomerById(@PathVariable String id) {
        return userService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

        @PostMapping("/customer")
    public User createCustomer(@RequestBody User user) {
        return userService.createCustomer(user);
    }

@PutMapping("/customer/{id}")
public ResponseEntity<User> updateCustomer(@PathVariable String id, @RequestBody User user) {
    User updated = userService.updateCustomer(id, user);
    if (updated != null) return ResponseEntity.ok(updated);
    return ResponseEntity.notFound().build();
}

// admin
 @GetMapping("/admin")
    public ResponseEntity<?> getAllAdmins(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "12") int limit,
                                          @RequestParam(required = false) String q,
                                          @RequestParam(required = false) Integer status) {
        Page<AdminDTO> admins = userService.getAllAdmins(page, limit, q, status);
        return ResponseEntity.ok(Map.of(
                "admins", admins.getContent(),
                "totalPages", admins.getTotalPages(),
                "total", admins.getTotalElements()
        ));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable String id) {
        return userService.getAdminById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin")
    public AdminDTO createAdmin(@RequestBody AdminDTO adminDTO) {
        return userService.createAdmin(adminDTO);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable String id, @RequestBody AdminDTO adminDTO) {
        AdminDTO updated = userService.updateAdmin(id, adminDTO);
        if (updated != null) return ResponseEntity.ok(updated);
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<?> toggleUserStatus(@PathVariable String id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Integer newStatus = user.getStatus() == 1 ? 0 : 1;
        User updated = userService.updateUserStatus(id, newStatus);

        return ResponseEntity.ok(Map.of(
                "id", updated.getId(),
                "status", updated.getStatus()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
