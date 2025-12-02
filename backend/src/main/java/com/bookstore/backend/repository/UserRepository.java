package com.bookstore.backend.repository;

import com.bookstore.backend.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);

  // Lấy theo role
  Page<User> findByRoleIn(List<Integer> roles, Pageable pageable);

  // Lấy theo email + role
  Page<User> findByEmailContainingIgnoreCaseAndRoleIn(
      String email, List<Integer> roles, Pageable pageable);

  // Lấy theo role + status
  Page<User> findByRoleInAndStatus(List<Integer> roles, Integer status, Pageable pageable);

  // Lấy theo email + role + status
  Page<User> findByEmailContainingIgnoreCaseAndRoleInAndStatus(
      String email, List<Integer> roles, Integer status, Pageable pageable);
}
