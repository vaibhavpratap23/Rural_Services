package com.gigfinder.controller;

import com.gigfinder.dto.UserAddressDTO;
import com.gigfinder.model.UserAddress;
import com.gigfinder.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    
    private final UserAddressService addressService;
    
    @PostMapping
    public ResponseEntity<?> addAddress(@Valid @RequestBody UserAddressDTO addressDTO) {
        try {
            UserAddress address = addressService.addAddress(addressDTO);
            return ResponseEntity.ok(convertToDTO(address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding address: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getUserAddresses() {
        try {
            List<UserAddress> addresses = addressService.getUserAddresses();
            return ResponseEntity.ok(addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching addresses: " + e.getMessage());
        }
    }
    
    @GetMapping("/default")
    public ResponseEntity<?> getDefaultAddress() {
        try {
            UserAddress address = addressService.getDefaultAddress();
            return ResponseEntity.ok(convertToDTO(address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching default address: " + e.getMessage());
        }
    }
    
    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(@PathVariable Long addressId, @Valid @RequestBody UserAddressDTO addressDTO) {
        try {
            UserAddress address = addressService.updateAddress(addressId, addressDTO);
            return ResponseEntity.ok(convertToDTO(address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating address: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        try {
            addressService.deleteAddress(addressId);
            return ResponseEntity.ok("Address deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting address: " + e.getMessage());
        }
    }
    
    @PutMapping("/{addressId}/default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable Long addressId) {
        try {
            UserAddress address = addressService.setDefaultAddress(addressId);
            return ResponseEntity.ok(convertToDTO(address));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error setting default address: " + e.getMessage());
        }
    }
    
    private UserAddressDTO convertToDTO(UserAddress address) {
        UserAddressDTO dto = new UserAddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setLocationLat(address.getLocationLat());
        dto.setLocationLng(address.getLocationLng());
        dto.setIsDefault(address.getIsDefault());
        dto.setAddressType(address.getAddressType());
        dto.setCreatedAt(address.getCreatedAt());
        return dto;
    }
}
