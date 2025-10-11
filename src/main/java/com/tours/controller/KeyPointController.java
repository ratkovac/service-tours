package com.tours.controller;

import com.tours.model.KeyPoint;
import com.tours.service.KeyPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/keypoints")
@CrossOrigin(origins = "*")
public class KeyPointController {
    
    private final KeyPointService keyPointService;
    
    @Autowired
    public KeyPointController(KeyPointService keyPointService) {
        this.keyPointService = keyPointService;
    }
    
    @PostMapping
    public ResponseEntity<?> createKeyPoint(@RequestBody Map<String, Object> request,
                                           @RequestHeader("X-Author-Id") Long autorId) {
        try {
            String naziv = (String) request.get("naziv");
            String opis = (String) request.get("opis");
            Double latitude = Double.valueOf(request.get("latitude").toString());
            Double longitude = Double.valueOf(request.get("longitude").toString());
            String slikaUrl = (String) request.get("slikaUrl");
            Long tourId = Long.valueOf(request.get("tourId").toString());
            
            KeyPoint keyPoint = keyPointService.createKeyPoint(naziv, opis, latitude, longitude, slikaUrl, tourId, autorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(keyPoint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Greška pri kreiranju ključne tačke: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<?> getKeyPointsByTour(@PathVariable Long tourId,
                                              @RequestHeader("X-Author-Id") Long autorId) {
        try {
            List<KeyPoint> keyPoints = keyPointService.getAllKeyPointsByTour(tourId, autorId);
            return ResponseEntity.ok(keyPoints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<KeyPoint>> getAllKeyPointsByAuthor(@RequestHeader("X-Author-Id") Long autorId) {
        List<KeyPoint> keyPoints = keyPointService.getAllKeyPointsByAuthor(autorId);
        return ResponseEntity.ok(keyPoints);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getKeyPointById(@PathVariable Long id,
                                           @RequestHeader("X-Author-Id") Long autorId) {
        Optional<KeyPoint> keyPoint = keyPointService.getKeyPointById(id, autorId);
        if (keyPoint.isPresent()) {
            return ResponseEntity.ok(keyPoint.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Ključna tačka nije pronađena"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateKeyPoint(@PathVariable Long id,
                                           @RequestBody Map<String, Object> request,
                                           @RequestHeader("X-Author-Id") Long autorId) {
        try {
            String naziv = (String) request.get("naziv");
            String opis = (String) request.get("opis");
            Double latitude = Double.valueOf(request.get("latitude").toString());
            Double longitude = Double.valueOf(request.get("longitude").toString());
            String slikaUrl = (String) request.get("slikaUrl");
            
            KeyPoint updatedKeyPoint = keyPointService.updateKeyPoint(id, naziv, opis, latitude, longitude, slikaUrl, autorId);
            return ResponseEntity.ok(updatedKeyPoint);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Greška pri ažuriranju ključne tačke: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKeyPoint(@PathVariable Long id,
                                          @RequestHeader("X-Author-Id") Long autorId) {
        boolean deleted = keyPointService.deleteKeyPoint(id, autorId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Ključna tačka je uspešno obrisana"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Ključna tačka nije pronađena"));
        }
    }
    
    @GetMapping("/tour/{tourId}/count")
    public ResponseEntity<?> getKeyPointCountByTour(@PathVariable Long tourId,
                                                   @RequestHeader("X-Author-Id") Long autorId) {
        try {
            long count = keyPointService.countKeyPointsByTour(tourId, autorId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getKeyPointCountByAuthor(@RequestHeader("X-Author-Id") Long autorId) {
        long count = keyPointService.countKeyPointsByAuthor(autorId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
