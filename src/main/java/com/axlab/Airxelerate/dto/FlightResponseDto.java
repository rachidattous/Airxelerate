package com.axlab.Airxelerate.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDto {
    private Long id;
    private String carrierCode;
    private String flightNumber;
    private LocalDate flightDate;
    private String origin;
    private String destination;
}

