package com.tours.service;

import com.tours.model.KeyPoint;
import com.tours.model.Tour;
import com.tours.repository.KeyPointRepository;
import com.tours.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KeyPointService {
    
    private final KeyPointRepository keyPointRepository;
    private final TourRepository tourRepository;
    
    @Autowired
    public KeyPointService(KeyPointRepository keyPointRepository, TourRepository tourRepository) {
        this.keyPointRepository = keyPointRepository;
        this.tourRepository = tourRepository;
    }
    
    public KeyPoint createKeyPoint(String naziv, String opis, Double latitude, Double longitude, 
                                  String slikaUrl, Long tourId, Long autorId) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (tour.isEmpty() || !tour.get().getAutorId().equals(autorId)) {
            throw new IllegalArgumentException("Tura nije pronađena ili ne pripada autoru");
        }
        
        KeyPoint keyPoint = new KeyPoint(naziv, opis, latitude, longitude, slikaUrl, tourId);
        return keyPointRepository.save(keyPoint);
    }
    
    @Transactional(readOnly = true)
    public List<KeyPoint> getAllKeyPointsByTour(Long tourId, Long autorId) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (tour.isEmpty() || !tour.get().getAutorId().equals(autorId)) {
            throw new IllegalArgumentException("Tura nije pronađena ili ne pripada autoru");
        }
        
        return keyPointRepository.findByTourIdAndAuthorId(tourId, autorId);
    }
    
    @Transactional(readOnly = true)
    public List<KeyPoint> getAllKeyPointsByAuthor(Long autorId) {
        return keyPointRepository.findByAuthorId(autorId);
    }
    
    @Transactional(readOnly = true)
    public Optional<KeyPoint> getKeyPointById(Long keyPointId, Long autorId) {
        return keyPointRepository.findByIdAndAuthorId(keyPointId, autorId);
    }
    
    public KeyPoint updateKeyPoint(Long keyPointId, String naziv, String opis, Double latitude, 
                                  Double longitude, String slikaUrl, Long autorId) {
        Optional<KeyPoint> existingKeyPoint = keyPointRepository.findByIdAndAuthorId(keyPointId, autorId);
        if (existingKeyPoint.isEmpty()) {
            throw new IllegalArgumentException("Ključna tačka nije pronađena");
        }
        
        KeyPoint keyPoint = existingKeyPoint.get();
        keyPoint.setNaziv(naziv);
        keyPoint.setOpis(opis);
        keyPoint.setLatitude(latitude);
        keyPoint.setLongitude(longitude);
        keyPoint.setSlikaUrl(slikaUrl);
        
        return keyPointRepository.save(keyPoint);
    }
    
    public boolean deleteKeyPoint(Long keyPointId, Long autorId) {
        Optional<KeyPoint> keyPoint = keyPointRepository.findByIdAndAuthorId(keyPointId, autorId);
        if (keyPoint.isPresent()) {
            keyPointRepository.delete(keyPoint.get());
            return true;
        }
        return false;
    }
    
    @Transactional(readOnly = true)
    public long countKeyPointsByTour(Long tourId, Long autorId) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (tour.isEmpty() || !tour.get().getAutorId().equals(autorId)) {
            return 0;
        }
        
        return keyPointRepository.countByTourId(tourId);
    }
    
    @Transactional(readOnly = true)
    public long countKeyPointsByAuthor(Long autorId) {
        return keyPointRepository.countByAuthorId(autorId);
    }
}

