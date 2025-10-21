package com.bookstore.backend.controller;
import com.bookstore.backend.dto.AddressDTO;
import com.bookstore.backend.entities.Address;
import com.bookstore.backend.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = "*")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Address>> getAddressesByUserId(@PathVariable String userId) {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

@GetMapping("/{userId}/{id}")
public ResponseEntity<Address> getAddressByIdAndUserId(
        @PathVariable String userId,
        @PathVariable String id) {

    return addressService.getAddressByIdAndUserId(id, userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

   @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody AddressDTO dto) {
        Address newAddress = addressService.createAddress(dto);
        return ResponseEntity.ok(newAddress);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(
            @PathVariable String id,
            @RequestBody AddressDTO dto) {
        Address updated = addressService.updateAddress(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
