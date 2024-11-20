package com.example.apibebakids.service.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.apibebakids.repository.mysql.WorkerRepository;
import com.example.apibebakids.model.mysql.ProductionWorker;
import com.example.apibebakids.model.mysql.ProductionWorkerCheckin;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkerService {
    @Autowired
    private WorkerRepository workerRepository;

    public List<ProductionWorker> getAllWorkersByLocation(String locationId) {
        return workerRepository.findAllWorkers(locationId);
    }


    @Transactional  // Ensures all operations are within a single transaction
    public void recordWorkerCheckins(List<ProductionWorkerCheckin> checkins) {


        for (ProductionWorkerCheckin checkin : checkins) {
            Long workerId = workerRepository.findWorkerIdByCode(checkin.getWorkerId());
            if (workerId != null) {
                workerRepository.saveCheckin(checkin,workerId);
            }

        }
    }


}
