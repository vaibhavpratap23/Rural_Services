package com.gigfinder.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class OtpService {
    
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public String generateOtp(String phoneNumber) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpStorage.put(phoneNumber, otp);
        
        // In real implementation, send SMS here
        System.out.println("OTP for " + phoneNumber + ": " + otp);
        
        return otp;
    }
    
    public boolean verifyOtp(String phoneNumber, String otp) {
        String storedOtp = otpStorage.get(phoneNumber);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(phoneNumber);
            return true;
        }
        return false;
    }
}
