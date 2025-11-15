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
public class FlightRequestDto {
    @NotBlank(message = "Carrier code is required")
    private String carrierCode;

    @NotBlank(message = "Flight number is required")
    @Size( max = 4, min = 4, message = "Flight number must contain exactly 4 characters")
    private String flightNumber;

    @NotBlank(message = "Flight date is required")
    private LocalDate flightDate;

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;
}
