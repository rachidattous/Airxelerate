package com.axlab.Airxelerate.service.Impl;

import com.axlab.Airxelerate.dto.FlightRequestDto;
import com.axlab.Airxelerate.dto.FlightResponseDto;
import com.axlab.Airxelerate.entity.Flight;
import com.axlab.Airxelerate.exception.FlightNotFoundException;
import com.axlab.Airxelerate.exception.InvalidFlightDataException;
import com.axlab.Airxelerate.repository.FlightRepository;
import com.axlab.Airxelerate.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public FlightResponseDto createFlight(FlightRequestDto flightRequestDto) {
        log.info("Attempting to add flight= {}", flightRequestDto.getCarrierCode());

        if(flightRequestDto.getFlightNumber() == null || flightRequestDto.getOrigin() == null || flightRequestDto.getDestination() == null){
            log.warn("Adding flight failed. Invalid flight data");
            throw new InvalidFlightDataException("Flight number, origin, and destination are required");
        }

        Flight flight = Flight.builder()
                .carrierCode(flightRequestDto.getCarrierCode())
                .flightNumber(flightRequestDto.getFlightNumber())
                .flightDate(flightRequestDto.getFlightDate())
                .origin(flightRequestDto.getOrigin())
                .destination(flightRequestDto.getDestination())
                .build();

        flight = flightRepository.save(flight);

        log.warn("Flight Added as: {}", flight.getFlightNumber());

        return FlightResponseDto.builder()
                .id(flight.getId())
                .carrierCode(flight.getCarrierCode())
                .flightNumber(flight.getFlightNumber())
                .flightDate(flight.getFlightDate())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .build();
    }

    @Override
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                        .orElseThrow(() -> new FlightNotFoundException(id));
        flightRepository.delete(flight);
        log.warn("Flight with id: {} deleted successfully", flight.getId());
    }

    @Override
    public Page<FlightResponseDto> getAllFlights(Pageable pageable) {
        log.debug("Fetch flights with pagination: page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Flight> page = flightRepository.findAll(pageable);

        if(page.isEmpty()){
            log.warn("No flights found for the requested page.");
            return Page.empty();
        }

        return page.map(f -> FlightResponseDto.builder()
                        .id(f.getId())
                        .carrierCode(f.getCarrierCode())
                        .flightNumber(f.getFlightNumber())
                        .flightDate(f.getFlightDate())
                        .origin(f.getOrigin())
                        .destination(f.getDestination())
                        .build()
        );
    }

    @Override
    public FlightResponseDto getFlightById(Long id) {
        Flight f = flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException(id));

        log.warn("Fetching flight with id: {}", id);

        return FlightResponseDto.builder()
                .id(f.getId())
                .carrierCode(f.getCarrierCode())
                .flightNumber(f.getFlightNumber())
                .flightDate(f.getFlightDate())
                .origin(f.getOrigin())
                .destination(f.getDestination())
                .build();
    }
}
