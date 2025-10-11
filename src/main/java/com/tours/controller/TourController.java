package com.tours.controller;

import com.tours.enums.Difficulty;
import com.tours.enums.TourStatus;
import com.tours.model.Tour;
import com.tours.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tours")
@CrossOrigin(origins = "*")
public class TourController {
    
    private final TourService tourService;
    
    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }
    
    @PostMapping
    public ResponseEntity<?> createTour(@RequestBody Map<String, String> request,
                                       @RequestHeader("X-Author-Id") Long autorId) {
        try {
            String naziv = request.get("naziv");
            String opis = request.get("opis");
            String tagovi = request.get("tagovi");
            String tezina = request.get("tezina");
            
            Difficulty difficulty = Difficulty.valueOf(tezina.toUpperCase());
            Tour tour = tourService.createTour(naziv, opis, tagovi, difficulty, autorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(tour);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Greška pri kreiranju ture: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Tour>> getAllToursByAuthor(@RequestHeader("X-Author-Id") Long autorId) {
        List<Tour> tours = tourService.getAllToursByAuthor(autorId);
        return ResponseEntity.ok(tours);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTourById(@PathVariable Long id, 
                                       @RequestHeader("X-Author-Id") Long autorId) {
        Optional<Tour> tour = tourService.getTourByIdAndAuthor(id, autorId);
        if (tour.isPresent()) {
            return ResponseEntity.ok(tour.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tura nije pronađena"));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getToursByStatus(@PathVariable String status, 
                                            @RequestHeader("X-Author-Id") Long autorId) {
        try {
            TourStatus tourStatus = TourStatus.valueOf(status.toUpperCase());
            List<Tour> tours = tourService.getToursByStatusAndAuthor(tourStatus, autorId);
            return ResponseEntity.ok(tours);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Neispravan status ture"));
        }
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<?> getToursByDifficulty(@PathVariable String difficulty, 
                                                 @RequestHeader("X-Author-Id") Long autorId) {
        try {
            Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
            List<Tour> tours = tourService.getToursByDifficultyAndAuthor(diff, autorId);
            return ResponseEntity.ok(tours);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Neispravna težina ture"));
        }
    }
    
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Tour>> getToursByTag(@PathVariable String tag, 
                                                   @RequestHeader("X-Author-Id") Long autorId) {
        List<Tour> tours = tourService.getToursByTagAndAuthor(tag, autorId);
        return ResponseEntity.ok(tours);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTour(@PathVariable Long id, 
                                      @RequestParam String naziv,
                                      @RequestParam String opis,
                                      @RequestParam String tagovi,
                                      @RequestParam String tezina,
                                      @RequestHeader("X-Author-Id") Long autorId) {
        Optional<Tour> existingTour = tourService.getTourByIdAndAuthor(id, autorId);
        if (existingTour.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tura nije pronađena"));
        }
        
        try {
            Difficulty difficulty = Difficulty.valueOf(tezina.toUpperCase());
            Tour tour = existingTour.get();
            tour.setNaziv(naziv);
            tour.setOpis(opis);
            tour.setTagovi(tagovi);
            tour.setTezina(difficulty);
            
            Tour updatedTour = tourService.updateTour(tour);
            return ResponseEntity.ok(updatedTour);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Greška pri ažuriranju ture: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTour(@PathVariable Long id, 
                                      @RequestHeader("X-Author-Id") Long autorId) {
        boolean deleted = tourService.deleteTour(id, autorId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Tura je uspešno obrisana"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tura nije pronađena"));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTourStats(@RequestHeader("X-Author-Id") Long autorId) {
        long totalTours = tourService.countToursByAuthor(autorId);
        long draftTours = tourService.countToursByStatusAndAuthor(TourStatus.DRAFT, autorId);
        long publishedTours = tourService.countToursByStatusAndAuthor(TourStatus.PUBLISHED, autorId);
        long archivedTours = tourService.countToursByStatusAndAuthor(TourStatus.ARCHIVED, autorId);
        
        Map<String, Object> stats = Map.of(
            "totalTours", totalTours,
            "draftTours", draftTours,
            "publishedTours", publishedTours,
            "archivedTours", archivedTours
        );
        
        return ResponseEntity.ok(stats);
    }
}
