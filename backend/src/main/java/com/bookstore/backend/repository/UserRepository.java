package com.bookstore.backend.repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.User;
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Lấy theo role
    Page<User> findByRoleIn(List<Integer> roles, Pageable pageable);

    // Lấy theo email + role
    Page<User> findByEmailContainingIgnoreCaseAndRoleIn(String email, List<Integer> roles, Pageable pageable);

    // Lấy theo role + status
    Page<User> findByRoleInAndStatus(List<Integer> roles, Integer status, Pageable pageable);

    // Lấy theo email + role + status
    Page<User> findByEmailContainingIgnoreCaseAndRoleInAndStatus(String email, List<Integer> roles, Integer status, Pageable pageable);
}