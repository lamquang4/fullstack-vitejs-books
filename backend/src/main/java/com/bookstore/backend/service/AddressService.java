package com.bookstore.backend.service;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.repository.AddressRepository;
import com.bookstore.backend.utils.ValidationUtils;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public Optional<Address> getAddressById(String id) {
        return addressRepository.findById(id);
    }

    public Address createAddress(Address address) {
          if (!ValidationUtils.validatePhone(address.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        return addressRepository.save(address);
    }

    public Address updateAddress(String id, Address address) {
        if (!ValidationUtils.validatePhone(address.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
    return addressRepository.findById(id)
                .map(existingAddress -> {
                    existingAddress.setFullname(address.getFullname());
                existingAddress.setPhone(address.getPhone());
                existingAddress.setSpeaddress(address.getSpeaddress());
                existingAddress.setWard(address.getWard());
                existingAddress.setCity(address.getCity());

                   return addressRepository.save(existingAddress);
                })
                .orElse(null);
    }

    public void deleteAddress(String id) {
        addressRepository.deleteById(id);
    }
}
