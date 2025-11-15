package com.axlab.Airxelerate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "flights")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "carrier_code", nullable = false)
    private String carrierCode;

    @Column(name = "flight_number", nullable = false, length = 4)
    private String flightNumber;

    @Column(name = "flight_date", nullable = false)
    private LocalDate flightDate;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;
}
