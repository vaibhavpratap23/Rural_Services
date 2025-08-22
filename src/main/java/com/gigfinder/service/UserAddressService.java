package com.gigfinder.service;

import com.gigfinder.dto.UserAddressDTO;
import com.gigfinder.model.User;
import com.gigfinder.model.UserAddress;
import com.gigfinder.repository.UserAddressRepository;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAddressService {
    
    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;
    
    public UserAddress addAddress(UserAddressDTO addressDTO) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // If this is the first address, make it default
        if (addressDTO.getIsDefault() == null) {
            List<UserAddress> existingAddresses = addressRepository.findByUser(currentUser);
            addressDTO.setIsDefault(existingAddresses.isEmpty());
        }
        
        // If setting as default, unset other defaults
        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            Optional<UserAddress> currentDefault = addressRepository.findByUserAndIsDefaultTrue(currentUser);
            currentDefault.ifPresent(address -> {
                address.setIsDefault(false);
                addressRepository.save(address);
            });
        }
        
        UserAddress address = UserAddress.builder()
                .user(currentUser)
                .addressLine1(addressDTO.getAddressLine1())
                .addressLine2(addressDTO.getAddressLine2())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry())
                .locationLat(addressDTO.getLocationLat())
                .locationLng(addressDTO.getLocationLng())
                .isDefault(addressDTO.getIsDefault())
                .addressType(addressDTO.getAddressType())
                .build();
        
        return addressRepository.save(address);
    }
    
    public List<UserAddress> getUserAddresses() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return addressRepository.findByUser(currentUser);
    }
    
    public UserAddress getDefaultAddress() {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return addressRepository.findByUserAndIsDefaultTrue(currentUser)
                .orElseThrow(() -> new RuntimeException("No default address found"));
    }
    
    public UserAddress updateAddress(Long addressId, UserAddressDTO addressDTO) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verify the address belongs to the current user
        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to update this address");
        }
        
        // If setting as default, unset other defaults
        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            Optional<UserAddress> currentDefault = addressRepository.findByUserAndIsDefaultTrue(currentUser);
            currentDefault.ifPresent(defaultAddress -> {
                if (!defaultAddress.getId().equals(addressId)) {
                    defaultAddress.setIsDefault(false);
                    addressRepository.save(defaultAddress);
                }
            });
        }
        
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());
        address.setLocationLat(addressDTO.getLocationLat());
        address.setLocationLng(addressDTO.getLocationLng());
        address.setIsDefault(addressDTO.getIsDefault());
        address.setAddressType(addressDTO.getAddressType());
        
        return addressRepository.save(address);
    }
    
    public void deleteAddress(Long addressId) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verify the address belongs to the current user
        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to delete this address");
        }
        
        // Don't allow deletion of the only address
        List<UserAddress> userAddresses = addressRepository.findByUser(currentUser);
        if (userAddresses.size() == 1) {
            throw new RuntimeException("Cannot delete the only address");
        }
        
        addressRepository.delete(address);
    }
    
    public UserAddress setDefaultAddress(Long addressId) {
        String username = SecurityUtil.getCurrentUsername();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserAddress address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Verify the address belongs to the current user
        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to modify this address");
        }
        
        // Unset current default
        Optional<UserAddress> currentDefault = addressRepository.findByUserAndIsDefaultTrue(currentUser);
        currentDefault.ifPresent(defaultAddress -> {
            defaultAddress.setIsDefault(false);
            addressRepository.save(defaultAddress);
        });
        
        // Set new default
        address.setIsDefault(true);
        return addressRepository.save(address);
    }
}
