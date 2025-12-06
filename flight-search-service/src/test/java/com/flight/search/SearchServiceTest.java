package com.flight.search;

import com.flight.search.controller.SearchController;
import com.flight.search.entity.Flight;
import com.flight.search.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchTest {

    @Mock FlightRepository repo;
    @InjectMocks SearchController ctrl;
    @Test void testAvail() {
        Flight f = new Flight(); f.setAvailableSeats(10);
        when(repo.findById(1L)).thenReturn(Optional.of(f));
        assertTrue(ctrl.check(1L, 2));
    }
}
