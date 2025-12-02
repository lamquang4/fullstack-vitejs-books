package com.bookstore.backend.controller;

import com.bookstore.backend.dto.UserDTO;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.service.UserService;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<?> getAllCustomers(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "12") int limit,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Integer status) {
    Page<UserDTO> customers = userService.getAllCustomers(page, limit, q, status);
    return ResponseEntity.ok(
        Map.of(
            "customers", customers.getContent(),
            "totalPages", customers.getTotalPages(),
            "total", customers.getTotalElements()));
  }

  // admin
  @GetMapping("/admin")
  public ResponseEntity<?> getAllAdmins(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "12") int limit,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Integer status) {
    Page<UserDTO> admins = userService.getAllAdmins(page, limit, q, status);
    return ResponseEntity.ok(
        Map.of(
            "admins", admins.getContent(),
            "totalPages", admins.getTotalPages(),
            "total", admins.getTotalElements()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
    return userService
        .getUserById(id)
        .map(userDTO -> ResponseEntity.ok(userDTO))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
    UserDTO created = userService.createUser(dto);
    return ResponseEntity.ok(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO dto) {
    UserDTO updated = userService.updateUser(id, dto);
    if (updated == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updated);
  }

  @PatchMapping("/status/{id}")
  public ResponseEntity<?> updateUserStatus(
      @PathVariable String id, @RequestBody Map<String, Integer> body) {
    Integer status = body.get("status");
    if (status == null) {
      throw new IllegalArgumentException("Tình trạng không để trống");
    }

    User updated = userService.updateUserStatus(id, status);

    return ResponseEntity.ok(
        Map.of(
            "id", updated.getId(),
            "status", updated.getStatus()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
