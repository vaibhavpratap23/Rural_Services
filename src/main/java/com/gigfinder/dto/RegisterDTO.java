package com.gigfinder.dto;

import jakarta.validation.constraints.*;
import com.gigfinder.model.enums.Role;
import java.math.BigDecimal;

public class RegisterDTO {
    
    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name cannot be empty")
    private String name;
    
    @NotNull(message = "Email is required") 
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email format is invalid")
    private String email;
    
    @NotNull(message = "Phone is required")
    private String phone;
    
    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    
    @NotNull(message = "Role is required")
    private Role role;
    
    // Client location fields
    private BigDecimal locationLat;
    private BigDecimal locationLng;
    
    // Worker fields
    private String aadhaarNumber;
    private String panNumber;
    private String address;
    private Integer radiusKm;
    private String verificationType; // "BASIC" or "FULL"
    
    // Constructors, getters, and setters...
    public RegisterDTO() {}
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public BigDecimal getLocationLat() { return locationLat; }
    public void setLocationLat(BigDecimal locationLat) { this.locationLat = locationLat; }
    
    public BigDecimal getLocationLng() { return locationLng; }
    public void setLocationLng(BigDecimal locationLng) { this.locationLng = locationLng; }
    
    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
    
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Integer getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Integer radiusKm) { this.radiusKm = radiusKm; }
    
    public String getVerificationType() { return verificationType; }
    public void setVerificationType(String verificationType) { this.verificationType = verificationType; }
}
