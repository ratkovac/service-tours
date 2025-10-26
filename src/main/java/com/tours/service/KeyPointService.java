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
                                  String slikaUrl, Long tourId, String autorUsername) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (tour.isEmpty() || !tour.get().getAutorUsername().equals(autorUsername)) {
            throw new IllegalArgumentException("Tura nije pronađena ili ne pripada autoru");
        }
        
        KeyPoint keyPoint = new KeyPoint(naziv, opis, latitude, longitude, slikaUrl, tourId);
        return keyPointRepository.save(keyPoint);
    }
    
    @Transactional(readOnly = true)
    public List<KeyPoint> getAllKeyPointsByTour(Long tourId, String autorUsername) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (tour.isEmpty() || !tour.get().getAutorUsername().equals(autorUsername)) {
            throw new IllegalArgumentException("Tura nije pronađena ili ne pripada autoru");
        }
        
        return keyPointRepository.findByTourIdAndAuthorUsername(tourId, autorUsername);
    }
    
    @Transactional(readOnly = true)
    public List<KeyPoint> getAllKeyPointsByAuthor(String autorUsername) {
        return keyPointRepository.findByAuthorUsername(autorUsername);
    }
    
    @Transactional(readOnly = true)
    public Optional<KeyPoint> getKeyPointById(Long keyPointId, String autorUsername) {
        return keyPointRepository.findByIdAndAuthorUsername(keyPointId, autorUsername);
    }
    
    public KeyPoint updateKeyPoint(Long keyPointId, String naziv, String opis, Double latitude, 
                                  Double longitude, String slikaUrl, String autorUsername) {
        Optional<KeyPoint> existingKeyPoint = keyPointRepository.findByIdAndAuthorUsername(keyPointId, autorUsername);
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
    
    public boolean deleteKeyPoint(Long keyPointId, String autorUsername) {
        Optional<KeyPoint> keyPoint = keyPointRepository.findByIdAndAuthorUsername(keyPointId, autorUsername);
        if (keyPoint.isPresent()) {
            keyPointRepository.delete(keyPoint.get());
            return true;
        }
        return false;
    }
    
    @Transactional(readOnly = true)
    public long countKeyPointsByTour(Long tourId, String autorUsername) {
        Optional<Tour> tour = tourRepository.findById(tourId);
        if (tour.isEmpty() || !tour.get().getAutorUsername().equals(autorUsername)) {
            return 0;
        }
        
        return keyPointRepository.countByTourId(tourId);
    }
    
    @Transactional(readOnly = true)
    public long countKeyPointsByAuthor(String autorUsername) {
        return keyPointRepository.countByAuthorUsername(autorUsername);
    }
}

