package com.kodekonveyor.tidyup;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRequestRepository extends JpaRepository<WorkRequest, Long> {
    //List<WorkRequest> findAllByCity(String city);
}
