package com.gigfinder.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAddressDTO {
    private Long id;
    
    @NotEmpty(message = "Address line 1 is required")
    private String addressLine1;
    
    private String addressLine2;
    
    @NotEmpty(message = "City is required")
    private String city;
    
    @NotEmpty(message = "State is required")
    private String state;
    
    @NotEmpty(message = "Postal code is required")
    private String postalCode;
    
    @NotEmpty(message = "Country is required")
    private String country;
    
    private Double locationLat;
    private Double locationLng;
    private Boolean isDefault;
    private String addressType;
    private LocalDateTime createdAt;
}
