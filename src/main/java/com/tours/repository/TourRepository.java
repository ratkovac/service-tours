package com.tours.repository;

import com.tours.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    
    List<Tour> findByAutorId(Long autorId);
    
    List<Tour> findByAutorIdAndStatus(Long autorId, String status);
    
    List<Tour> findByAutorIdAndTezina(Long autorId, String tezina);
    
    @Query("SELECT t FROM Tour t WHERE t.tagovi LIKE %:tag% AND t.autorId = :autorId")
    List<Tour> findByTagAndAutorId(@Param("tag") String tag, @Param("autorId") Long autorId);
    
    long countByAutorId(Long autorId);
    
    long countByAutorIdAndStatus(Long autorId, String status);
}
