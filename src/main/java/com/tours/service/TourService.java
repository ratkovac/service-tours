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

    // Promenjeno: String autorUsername umesto Long autorId
    public Tour createTour(String naziv, String opis, String tagovi, Difficulty tezina, String autorUsername) {
        Tour tour = new Tour(naziv, opis, tagovi, tezina, autorUsername);
        return tourRepository.save(tour);
    }

    @Transactional(readOnly = true)
    // Promenjeno: String autorUsername
    public List<Tour> getAllToursByAuthor(String autorUsername) {
        return tourRepository.findByAutorUsername(autorUsername);
    }

    @Transactional(readOnly = true)
    // Promenjeno: String autorUsername
    public Optional<Tour> getTourByIdAndAuthor(Long tourId, String autorUsername) {
        return tourRepository.findById(tourId)
                .filter(tour -> tour.getAutorUsername().equals(autorUsername));
    }

    @Transactional(readOnly = true)
    // Promenjeno: String autorUsername
    public List<Tour> getToursByStatusAndAuthor(TourStatus status, String autorUsername) {
        return tourRepository.findByAutorUsernameAndStatus(autorUsername, status.name());
    }

    @Transactional(readOnly = true)
    // Promenjeno: String autorUsername
    public List<Tour> getToursByDifficultyAndAuthor(Difficulty difficulty, String autorUsername) {
        return tourRepository.findByAutorUsernameAndTezina(autorUsername, difficulty.name());
    }

    @Transactional(readOnly = true)
    // Promenjeno: String autorUsername
    public List<Tour> getToursByTagAndAuthor(String tag, String autorUsername) {
        return tourRepository.findByTagAndAutorUsername(tag, autorUsername);
    }

    public Tour updateTour(Tour tour) {
        return tourRepository.save(tour);
    }

    // Promenjeno: String autorUsername
    public boolean deleteTour(Long tourId, String autorUsername) {
        Optional<Tour> tour = getTourByIdAndAuthor(tourId, autorUsername);
        if (tour.isPresent()) {
            tourRepository.delete(tour.get());
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    // Promenjeno: String autorUsername
    public long countToursByAuthor(String autorUsername) {
        return tourRepository.countByAutorUsername(autorUsername);
    }

    @Transactional(readOnly = true)
    // Promenjeno: String autorUsername
    public long countToursByStatusAndAuthor(TourStatus status, String autorUsername) {
        return tourRepository.countByAutorUsernameAndStatus(autorUsername, status.name());
    }
}