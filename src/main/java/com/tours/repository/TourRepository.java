package com.tours.repository;

import com.tours.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {

    // Promenjeno: findByAutorUsername umesto findByAutorId
    List<Tour> findByAutorUsername(String autorUsername);

    // Promenjeno: findByAutorUsernameAndStatus
    List<Tour> findByAutorUsernameAndStatus(String autorUsername, String status);

    // Promenjeno: findByAutorUsernameAndTezina
    List<Tour> findByAutorUsernameAndTezina(String autorUsername, String tezina);

    // Promenjeno: Query mora da koristi autorUsername
    @Query("SELECT t FROM Tour t WHERE t.tagovi LIKE %:tag% AND t.autorUsername = :autorUsername")
    List<Tour> findByTagAndAutorUsername(@Param("tag") String tag, @Param("autorUsername") String autorUsername);

    // Promenjeno: countByAutorUsername
    long countByAutorUsername(String autorUsername);

    // Promenjeno: countByAutorUsernameAndStatus
    long countByAutorUsernameAndStatus(String autorUsername, String status);
}