package com.amazonlocker.repository;

import com.amazonlocker.entity.Compartment;
import com.amazonlocker.entity.CompartmentSize;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompartmentRepository extends JpaRepository<Compartment, Long> {

    long countByCompartmentSize(CompartmentSize compartmentSize);

    List<Compartment> findByIsOccupiedFalse();

    Optional<CompartmentStatusView> findCompartmentStatusByCompartmentId(Long compartmentId);

    interface CompartmentStatusView {
        CompartmentSize getCompartmentSize();

        boolean getIsOccupied();
    }
}
