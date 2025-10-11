package com.tours.service;

import com.tours.enums.Difficulty;
import com.tours.enums.TourStatus;
import com.tours.model.Tour;
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
class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourService tourService;

    private Tour testTour;
    private Long testAuthorId;

    @BeforeEach
    void setUp() {
        testAuthorId = 1L;
        testTour = new Tour(
            "Test Tura",
            "Opis test ture",
            "planina, priroda",
            Difficulty.MEDIUM,
            testAuthorId
        );
        testTour.setId(1L);
    }

    @Test
    void createTour_ShouldCreateTourWithDraftStatusAndZeroPrice() {
        when(tourRepository.save(any(Tour.class))).thenReturn(testTour);

        Tour result = tourService.createTour(
            "Test Tura",
            "Opis test ture",
            "planina, priroda",
            Difficulty.MEDIUM,
            testAuthorId
        );

        assertNotNull(result);
        assertEquals(TourStatus.DRAFT, result.getStatus());
        assertEquals(0, result.getCijena().compareTo(java.math.BigDecimal.ZERO));
        assertEquals(testAuthorId, result.getAutorId());
        verify(tourRepository, times(1)).save(any(Tour.class));
    }

    @Test
    void getAllToursByAuthor_ShouldReturnToursForAuthor() {
        List<Tour> expectedTours = Arrays.asList(testTour);
        when(tourRepository.findByAutorId(testAuthorId)).thenReturn(expectedTours);

        List<Tour> result = tourService.getAllToursByAuthor(testAuthorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTour.getId(), result.get(0).getId());
        verify(tourRepository, times(1)).findByAutorId(testAuthorId);
    }
}
