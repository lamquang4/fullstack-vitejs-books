package com.bookstore.backend.service;
import com.bookstore.backend.dto.AddressDTO;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.AddressRepository;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.ValidationUtils;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
         this.userRepository = userRepository;
    }
    
    public List<Address> getAddressesByUserId(String userId) {
    return addressRepository.findByUserId(userId);
}

public Optional<Address> getAddressByIdAndUserId(String id, String userId) {
    return addressRepository.findByIdAndUserId(id, userId);
}

 public Address createAddress(AddressDTO dto) {
        if (!ValidationUtils.validatePhone(dto.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Address address = Address.builder()
                .fullname(dto.getFullname())
                .phone(dto.getPhone())
                .speaddress(dto.getSpeaddress())
                .ward(dto.getWard())
                .city(dto.getCity())
                .user(user)
                .build();

        return addressRepository.save(address);
    }
public Address updateAddress(String id, AddressDTO dto) {
        if (!ValidationUtils.validatePhone(dto.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        return addressRepository.findById(id)
                .map(existingAddress -> {
                    existingAddress.setFullname(dto.getFullname());
                    existingAddress.setPhone(dto.getPhone());
                    existingAddress.setSpeaddress(dto.getSpeaddress());
                    existingAddress.setWard(dto.getWard());
                    existingAddress.setCity(dto.getCity());

                    if (dto.getUserId() != null) {
                        User user = userRepository.findById(dto.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                        existingAddress.setUser(user);
                    }

                    return addressRepository.save(existingAddress);
                })
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    }

    public void deleteAddress(String id) {
        addressRepository.deleteById(id);
    }
}
