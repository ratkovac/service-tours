package com.tours.repository;

import com.tours.model.KeyPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeyPointRepository extends JpaRepository<KeyPoint, Long> {
    
    List<KeyPoint> findByTourId(Long tourId);
    
    @Query("SELECT kp FROM KeyPoint kp JOIN Tour t ON kp.tourId = t.id WHERE t.autorId = :autorId")
    List<KeyPoint> findByAuthorId(@Param("autorId") Long autorId);
    
    @Query("SELECT kp FROM KeyPoint kp JOIN Tour t ON kp.tourId = t.id WHERE kp.tourId = :tourId AND t.autorId = :autorId")
    List<KeyPoint> findByTourIdAndAuthorId(@Param("tourId") Long tourId, @Param("autorId") Long autorId);
    
    @Query("SELECT kp FROM KeyPoint kp JOIN Tour t ON kp.tourId = t.id WHERE kp.id = :keyPointId AND t.autorId = :autorId")
    java.util.Optional<KeyPoint> findByIdAndAuthorId(@Param("keyPointId") Long keyPointId, @Param("autorId") Long autorId);
    
    long countByTourId(Long tourId);
    
    @Query("SELECT COUNT(kp) FROM KeyPoint kp JOIN Tour t ON kp.tourId = t.id WHERE t.autorId = :autorId")
    long countByAuthorId(@Param("autorId") Long autorId);
}
