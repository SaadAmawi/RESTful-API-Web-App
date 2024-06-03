package com.example.demo.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.TreePlantingRecord;
import com.example.demo.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    String authenticateUser(LoginRequest loginRequest);

    String uploadTreePhoto(String username, MultipartFile img, String treeSpecies);

    List<TreePlantingRecord> getUserPlantingHistory(String username);

    String getUserRewards(String username);
}


