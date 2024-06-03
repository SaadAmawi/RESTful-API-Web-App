package com.example.demo.Controller;

import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.dto.LoginRequest;
import com.example.demo.model.TreePlantingRecord;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestBody LoginRequest loginRequest) {
        String token = userService.authenticateUser(loginRequest);
        if (token != null) {
            return token;

        } else {
            return "Authentication failed";
        }
    }
    @PostMapping("/uploadTree")
    public String uploadTreePhoto(
            @RequestParam("photo") MultipartFile img,
            @RequestParam("treeSpecies") String treeSpecies,
            @RequestParam("username") String username) {
        return userService.uploadTreePhoto(username, img,  treeSpecies);
    }

    @GetMapping("/plantingHistory")
    public List<TreePlantingRecord> getUserPlantingHistory(@RequestParam String username) {
        return userService.getUserPlantingHistory(username);
    }

        @GetMapping("/rewards")
        public String getUserRewards(@RequestParam("username")String username) {
            return userService.getUserRewards(username);
    }
}
