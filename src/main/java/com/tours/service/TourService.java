package com.tours.service;

import com.tours.enums.Difficulty;
import com.tours.enums.TourStatus;
import com.tours.model.Tour;
import com.tours.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TourService {
    
    private final TourRepository tourRepository;
    
    @Autowired
    public TourService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }
    
    public Tour createTour(String naziv, String opis, String tagovi, Difficulty tezina, Long autorId) {
        Tour tour = new Tour(naziv, opis, tagovi, tezina, autorId);
        return tourRepository.save(tour);
    }
    
    @Transactional(readOnly = true)
    public List<Tour> getAllToursByAuthor(Long autorId) {
        return tourRepository.findByAutorId(autorId);
    }
    
    @Transactional(readOnly = true)
    public Optional<Tour> getTourByIdAndAuthor(Long tourId, Long autorId) {
        return tourRepository.findById(tourId)
                .filter(tour -> tour.getAutorId().equals(autorId));
    }
    
    @Transactional(readOnly = true)
    public List<Tour> getToursByStatusAndAuthor(TourStatus status, Long autorId) {
        return tourRepository.findByAutorIdAndStatus(autorId, status.name());
    }
    
    @Transactional(readOnly = true)
    public List<Tour> getToursByDifficultyAndAuthor(Difficulty difficulty, Long autorId) {
        return tourRepository.findByAutorIdAndTezina(autorId, difficulty.name());
    }
    
    @Transactional(readOnly = true)
    public List<Tour> getToursByTagAndAuthor(String tag, Long autorId) {
        return tourRepository.findByTagAndAutorId(tag, autorId);
    }
    
    public Tour updateTour(Tour tour) {
        return tourRepository.save(tour);
    }
    
    public boolean deleteTour(Long tourId, Long autorId) {
        Optional<Tour> tour = getTourByIdAndAuthor(tourId, autorId);
        if (tour.isPresent()) {
            tourRepository.delete(tour.get());
            return true;
        }
        return false;
    }
    
    @Transactional(readOnly = true)
    public long countToursByAuthor(Long autorId) {
        return tourRepository.countByAutorId(autorId);
    }
    
    @Transactional(readOnly = true)
    public long countToursByStatusAndAuthor(TourStatus status, Long autorId) {
        return tourRepository.countByAutorIdAndStatus(autorId, status.name());
    }
}
