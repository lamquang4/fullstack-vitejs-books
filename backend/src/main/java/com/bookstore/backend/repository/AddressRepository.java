package com.bookstore.backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.entities.User;
@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
      void deleteByUser(User user);

      List<Address> findByUser(User user);
}