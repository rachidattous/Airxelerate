package com.axlab.Airxelerate.service;

import com.axlab.Airxelerate.dto.FlightRequestDto;
import com.axlab.Airxelerate.dto.FlightResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightService {
    FlightResponseDto createFlight(FlightRequestDto flightRequestDto);
    void deleteFlight(Long id);
    Page<FlightResponseDto> getAllFlights(Pageable pageable);
    FlightResponseDto getFlightById(Long id);
}
