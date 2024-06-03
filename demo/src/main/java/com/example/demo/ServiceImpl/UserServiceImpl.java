package com.example.demo.ServiceImpl;

import com.example.demo.Repository.RewardRepository;
import com.example.demo.Repository.TreePlantingRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserService;
import com.example.demo.Service.locationServices;
import com.example.demo.config.SecurityConfig;
import com.example.demo.dto.LoginRequest;
import com.example.demo.model.Reward;
import com.example.demo.model.TreePlantingRecord;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {
    LocalDate currentDate = LocalDate.now();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TreePlantingRepository treePlantingRecordRepository;

    @Autowired
    private locationServices locationServices;

    @Autowired
    private SecurityConfig security;

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    List<String> loggedIn = new ArrayList<>();

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);

    }

    @Override
    public String authenticateUser(LoginRequest loginRequest) {
        User users = userRepository.findByUsername(loginRequest.getUsername());
        logger.info("user found:" + users);

        if (users != null) {
            User user = users;
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {

                user.setToken("dummy-token: " + user.getUsername());
                loggedIn.add(users.getUsername());
                userRepository.save(user);
                return "dummy-token: " + user.getUsername(); //HERE WE NEED TO EITHER USE OAuth2.0 OR LIKE JWT OR SOMETHING TO GENERATE THE TOKENS - Saad
            }
        }
        return null;
    }

    @Override
    public String uploadTreePhoto(String username, MultipartFile img, String treeSpecies) {
        logger.info("Uploading tree photo for token: " + username);
        for(String m : loggedIn){
        logger.info("Uploading tree photo for token: " + m);
}
        User users = userRepository.findByUsername(username);


        if (users!= null && loggedIn.contains(users.getUsername())) {
            try{
                byte[] imgBytes = img.getBytes();
            TreePlantingRecord record = new TreePlantingRecord();
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] fileHashBytes = digest.digest(imgBytes);
                String fileHash = security.bytesToHex(fileHashBytes);

                if (treePlantingRecordRepository.existsByUserAndFileHash(users, fileHash)) {
                    return "Duplicate file detected";
                }
            record.setPlantingDate(currentDate);
            record.setUser(users);
            record.setPhoto(imgBytes);
            record.setGpsLocation(locationServices.getLocation());
            record.setTreeSpecies(treeSpecies);
            record.setFileHash(fileHash);
            treePlantingRecordRepository.save(record);
            return "Tree planted record uploaded successfully";
            } catch (IOException e) {
                throw new RuntimeException("Could not read the file. Error: " + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return "Invalid token";
    }

    @Override
    public List<TreePlantingRecord> getUserPlantingHistory(String username) {
        logger.info(username);
        User users = userRepository.findByUsername(username);
        if (users!=null && loggedIn.contains(users.getUsername())) {
            return treePlantingRecordRepository.findByUser(users);
        }
        return null;
    }

    @Override
    public String getUserRewards(String username) {
        User users = userRepository.findByUsername(username);


        if (users!=null && loggedIn.contains(users.getUsername())) {
            List<TreePlantingRecord> records = treePlantingRecordRepository.findByUser(users);
            int rewardPoints = calculateTotalMultiplier(records);

            Reward reward = rewardRepository.findByUser(users).orElse(new Reward());
            reward.setUser(users);
            reward.setTotalRewards(rewardPoints);

            return "Current Reward Points: "+rewardPoints+", Number of Trees Planted: "+records.size();
        }
        return "Incorrect ID or Token";
    }

    private int calculateTotalMultiplier(List<TreePlantingRecord> records) {
        int rewardPoints = 0;
        for (TreePlantingRecord record : records) {
            int basePoints = 10;
            int locationMultiplier = record.getGpsLocation().equalsIgnoreCase("Private Network") ? 5 : 10; // 10 for actual location, 5 for private network
            int speciesMultiplier = getSpeciesMultiplier(record.getTreeSpecies());
            rewardPoints += basePoints * locationMultiplier * speciesMultiplier;
        }
        return rewardPoints;
    }

    private int getSpeciesMultiplier(String treeSpecies) {
        switch (treeSpecies.toLowerCase()) {
            case "oak":
                return 2;
            case "pine":
                return 3;
            case "olive":
                return 4;
            case "spruce":
                return 5;
                default:
                return 1;
        }
    }
}

