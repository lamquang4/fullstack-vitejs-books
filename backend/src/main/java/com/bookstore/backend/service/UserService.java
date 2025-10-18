package com.bookstore.backend.service;
import com.bookstore.backend.dto.AddressDTO;
import com.bookstore.backend.dto.AdminDTO;
import com.bookstore.backend.dto.CustomerDTO;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.AddressRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.utils.ValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AddressRepository addressRepository, OrderRepository orderRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // customer
    public Page<CustomerDTO> getAllCustomers(int page, int limit, String q, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit);
    List<Integer> roles = List.of(3);

    Page<User> usersPage;
    if (q != null && !q.isEmpty() && status != null) {
        usersPage = userRepository.findByEmailContainingIgnoreCaseAndRoleInAndStatus(q, roles, status, pageable);
    } else if (q != null && !q.isEmpty()) {
        usersPage = userRepository.findByEmailContainingIgnoreCaseAndRoleIn(q, roles, pageable);
    } else if (status != null) {
        usersPage = userRepository.findByRoleInAndStatus(roles, status, pageable);
    } else {
        usersPage = userRepository.findByRoleIn(roles, pageable);
    }

    List<CustomerDTO> dtos = usersPage.getContent().stream()
            .map(user -> CustomerDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .build())
            .toList();

    return new PageImpl<>(dtos, pageable, usersPage.getTotalElements());
}

    public Optional<User> getCustomerById(String id) {
        return userRepository.findById(id);
    }

       public User createCustomer(User user) {
        if (!ValidationUtils.validateEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }
       
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setRole(3);
        return userRepository.save(user);
    }

    public User updateCustomer(String id, User user) {
    if (!ValidationUtils.validateEmail(user.getEmail())) {
        throw new IllegalArgumentException("Invalid email");
    }
    return userRepository.findById(id)
            .map(existingUser -> {
                existingUser.setEmail(user.getEmail());
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                existingUser.setStatus(user.getStatus());
                existingUser.setRole(3);
                return userRepository.save(existingUser);
            })
            .orElse(null);
}

    // admin
public Page<AdminDTO> getAllAdmins(int page, int limit, String q, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<Integer> roles = List.of(0, 1, 2); // admin roles

        Page<User> adminsPage;

        if (q != null && !q.isEmpty() && status != null) {
            adminsPage = userRepository.findByEmailContainingIgnoreCaseAndRoleInAndStatus(q, roles, status, pageable);
        } else if (q != null && !q.isEmpty()) {
            adminsPage = userRepository.findByEmailContainingIgnoreCaseAndRoleIn(q, roles, pageable);
        } else if (status != null) {
            adminsPage = userRepository.findByRoleInAndStatus(roles, status, pageable);
        } else {
            adminsPage = userRepository.findByRoleIn(roles, pageable);
        }

        List<AdminDTO> dtos = adminsPage.getContent().stream().map(user -> {
            AdminDTO dto = AdminDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .build();

            Address address = addressRepository.findByUser(user).stream().findFirst().orElse(null);
            if (address != null) {
                dto.setAddress(new AddressDTO(
                        address.getId(),
                        address.getFullname(),
                        address.getPhone(),
                        address.getSpeaddress(),
                        address.getWard(),
                        address.getCity()
                ));
            }

            return dto;
        }).toList();

        return new PageImpl<>(dtos, pageable, adminsPage.getTotalElements());
    }

    public Optional<AdminDTO> getAdminById(String id) {
        return userRepository.findById(id).map(user -> {
            AdminDTO dto = AdminDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .build();
  Address address = addressRepository.findByUser(user).stream().findFirst().orElse(null);
            if (address != null) {
                dto.setAddress(new AddressDTO(
                        address.getId(),
                        address.getFullname(),
                        address.getPhone(),
                        address.getSpeaddress(),
                        address.getWard(),
                        address.getCity()
                ));
            }
            return dto;
        });
    }

    public AdminDTO createAdmin(AdminDTO adminDTO) {
        if (!ValidationUtils.validateEmail(adminDTO.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }

        User user = User.builder()
                .email(adminDTO.getEmail())
                .password(passwordEncoder.encode(adminDTO.getPassword()))
                .status(adminDTO.getStatus() != null ? adminDTO.getStatus() : 1)
                .role(adminDTO.getRole())
                .build();

        User savedUser = userRepository.save(user);

        if (adminDTO.getAddress() != null) {
            AddressDTO a = adminDTO.getAddress();
            Address address = Address.builder()
                    .fullname(a.getFullname())
                    .phone(a.getPhone())
                    .speaddress(a.getSpeaddress())
                    .ward(a.getWard())
                    .city(a.getCity())
                    .user(savedUser)
                    .build();
            addressRepository.save(address);
        }

        adminDTO.setId(savedUser.getId());
        return adminDTO;
    }

    public AdminDTO updateAdmin(String id, AdminDTO adminDTO) {
        return userRepository.findById(id).map(user -> {
            if (!ValidationUtils.validateEmail(adminDTO.getEmail())) {
                throw new IllegalArgumentException("Invalid email");
            }
            user.setEmail(adminDTO.getEmail());
            if (adminDTO.getPassword() != null && !adminDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
            }
            user.setRole(adminDTO.getRole());
            user.setStatus(adminDTO.getStatus());
            userRepository.save(user);

            if (adminDTO.getAddress() != null) {
            Address address = addressRepository.findByUser(user).stream().findFirst().orElse(null);

                AddressDTO a = adminDTO.getAddress();
                address.setFullname(a.getFullname());
                address.setPhone(a.getPhone());
                address.setSpeaddress(a.getSpeaddress());
                address.setWard(a.getWard());
                address.setCity(a.getCity());
                addressRepository.save(address);
            }

            adminDTO.setId(user.getId());
            return adminDTO;
        }).orElse(null);
    }

public User updateUserStatus(String id, Integer status) {
    return userRepository.findById(id)
            .map(user -> {
                user.setStatus(status); 
                return userRepository.save(user);
            })
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
}

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Xóa user
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user"));

        if (orderRepository.existsByUser(user)) {
            throw new IllegalStateException("This user cannot be deleted because they have existing orders");
        }

        // xóa tất cả address và cart trước
        addressRepository.deleteByUser(user);
        cartRepository.deleteByUser(user);
        userRepository.deleteById(id);
    }
}
