package com.flight.booking.search;

import com.flight.booking.search.controller.SearchController;
import com.flight.booking.search.entity.Flight;
import com.flight.booking.search.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock FlightRepository repo;
    @InjectMocks SearchController ctrl;
    @Test void testAvail() {
        Flight f = new Flight(); f.setAvailableSeats(10);
        when(repo.findById(1L)).thenReturn(Optional.of(f));
        assertTrue(ctrl.check(1L, 2));
    }
}
