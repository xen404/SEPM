package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import java.util.Objects;

public class PriceDto {
    private char sector;
    private float price;

    public PriceDto() {}

    public PriceDto(char sector, float price) {
        this.sector = sector;
        this.price = price;
    }

    public char getSector() {
        return sector;
    }

    public void setSector(char sector) {
        this.sector = sector;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceDto priceDto = (PriceDto) o;
        return sector == priceDto.sector &&
            Float.compare(priceDto.price, price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sector, price);
    }
}
