package com.example.demo.Repository;

import com.example.demo.model.TreePlantingRecord;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreePlantingRepository extends JpaRepository<TreePlantingRecord, Long> {
    List<TreePlantingRecord> findByUser(User user);
    int countByUser(User user);
    boolean existsByUserAndFileHash(User user, String fileHash);

}
