package com.tours.service;

import com.tours.model.KeyPoint;
import com.tours.model.Tour;
import com.tours.repository.KeyPointRepository;
import com.tours.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeyPointServiceTest {

    @Mock
    private KeyPointRepository keyPointRepository;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private KeyPointService keyPointService;

    private KeyPoint testKeyPoint;
    private Tour testTour;
    private String testAutorUsername;
    private Long testTourId;

    @BeforeEach
    void setUp() {
        testAutorUsername = "testuser";
        testTourId = 1L;
        
        testTour = new Tour("Test Tura", "Opis test ture", "planina, priroda", 
                          com.tours.enums.Difficulty.MEDIUM, testAutorUsername);
        testTour.setId(testTourId);
        
        testKeyPoint = new KeyPoint("Muzej", "Opis muzeja", 44.7866, 20.4489, 
                                  "http://example.com/slika.jpg", testTourId);
        testKeyPoint.setId(1L);
    }

    @Test
    void createKeyPoint_ShouldCreateKeyPointSuccessfully() {
        when(tourRepository.findById(testTourId)).thenReturn(Optional.of(testTour));
        when(keyPointRepository.save(any(KeyPoint.class))).thenReturn(testKeyPoint);

        KeyPoint result = keyPointService.createKeyPoint(
            "Muzej", "Opis muzeja", 44.7866, 20.4489, 
            "http://example.com/slika.jpg", testTourId, testAutorUsername
        );

        assertNotNull(result);
        assertEquals("Muzej", result.getNaziv());
        assertEquals(44.7866, result.getLatitude());
        assertEquals(20.4489, result.getLongitude());
        assertEquals(testTourId, result.getTourId());
        verify(keyPointRepository, times(1)).save(any(KeyPoint.class));
    }

    @Test
    void getAllKeyPointsByTour_ShouldReturnKeyPointsForTour() {
        when(tourRepository.findById(testTourId)).thenReturn(Optional.of(testTour));
        when(keyPointRepository.findByTourIdAndAuthorUsername(testTourId, testAutorUsername))
            .thenReturn(Arrays.asList(testKeyPoint));

        List<KeyPoint> result = keyPointService.getAllKeyPointsByTour(testTourId, testAutorUsername);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testKeyPoint.getId(), result.get(0).getId());
        verify(keyPointRepository, times(1)).findByTourIdAndAuthorUsername(testTourId, testAutorUsername);
    }

    @Test
    void createKeyPoint_ShouldThrowExceptionWhenTourNotFound() {
        when(tourRepository.findById(testTourId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            keyPointService.createKeyPoint(
                "Muzej", "Opis muzeja", 44.7866, 20.4489, 
                "http://example.com/slika.jpg", testTourId, testAutorUsername
            );
        });
    }

    @Test
    void deleteKeyPoint_ShouldReturnTrueWhenKeyPointDeleted() {
        when(keyPointRepository.findByIdAndAuthorUsername(1L, testAutorUsername)).thenReturn(Optional.of(testKeyPoint));

        boolean result = keyPointService.deleteKeyPoint(1L, testAutorUsername);

        assertTrue(result);
        verify(keyPointRepository, times(1)).findByIdAndAuthorUsername(1L, testAutorUsername);
        verify(keyPointRepository, times(1)).delete(testKeyPoint);
    }

    @Test
    void deleteKeyPoint_ShouldReturnFalseWhenKeyPointNotFound() {
        when(keyPointRepository.findByIdAndAuthorUsername(999L, testAutorUsername)).thenReturn(Optional.empty());

        boolean result = keyPointService.deleteKeyPoint(999L, testAutorUsername);

        assertFalse(result);
        verify(keyPointRepository, times(1)).findByIdAndAuthorUsername(999L, testAutorUsername);
        verify(keyPointRepository, never()).delete(any());
    }
}

