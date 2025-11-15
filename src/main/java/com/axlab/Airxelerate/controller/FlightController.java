package com.axlab.Airxelerate.controller;

import com.axlab.Airxelerate.dto.FlightRequestDto;
import com.axlab.Airxelerate.dto.FlightResponseDto;
import com.axlab.Airxelerate.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<Page<FlightResponseDto>> getAllFlights(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ){
        Sort sorting = Sort.by(
                Arrays.stream(sort.split(";"))
                        .map(s -> {
                            String[] parts = s.split(",");
                            Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
                            return new Sort.Order(direction, parts[0]);
                        }).toList()
        );

        Page<FlightResponseDto> flights = flightService.getAllFlights(PageRequest.of(page, size, sorting));
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDto> getFlight(@PathVariable Long id){
        FlightResponseDto flight= flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlightResponseDto> createFlight(@Valid @RequestBody FlightRequestDto flightRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.createFlight(flightRequestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id){
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}
