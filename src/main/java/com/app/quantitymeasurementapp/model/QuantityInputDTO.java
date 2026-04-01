package com.app.quantitymeasurementapp.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class QuantityInputDTO {

    @NotNull(message = "thisQuantityDTO must not be null")
    @Valid
    private QuantityDTO thisQuantityDTO;

    @NotNull(message = "thatQuantityDTO must not be null")
    @Valid
    private QuantityDTO thatQuantityDTO;

    public QuantityInputDTO() {}

    public QuantityInputDTO(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        this.thisQuantityDTO = thisQuantityDTO;
        this.thatQuantityDTO = thatQuantityDTO;
    }

    public QuantityDTO getThisQuantityDTO() { return thisQuantityDTO; }
    public QuantityDTO getThatQuantityDTO() { return thatQuantityDTO; }

    public void setThisQuantityDTO(QuantityDTO thisQuantityDTO) { this.thisQuantityDTO = thisQuantityDTO; }
    public void setThatQuantityDTO(QuantityDTO thatQuantityDTO) { this.thatQuantityDTO = thatQuantityDTO; }
}
