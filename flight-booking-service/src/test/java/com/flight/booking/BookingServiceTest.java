package com.flight.booking;

import com.flight.booking.client.*;
import com.flight.booking.dto.BookingRequest;
import com.flight.booking.entity.Booking;
import com.flight.booking.repository.BookingRepository;
import com.flight.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock BookingRepository repo;
    @Mock SearchClient search;
    @Mock PaymentClient pay;
    @InjectMocks BookingService s;

    @Test void testSuccess() {
        when(search.checkAvailability(1L, 1)).thenReturn(true);
        when(repo.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Booking b = s.createBooking(new BookingRequest(1L, 1L, 1, 100.0));
        assertEquals("PENDING", b.getStatus());
        verify(pay, times(1)).initiatePayment(any());
    }
}
