package lt.vinted.entity;

import lt.vinted.enumerated.ShipmentSize;

import java.util.Objects;

public class ShippingProvider {
    // Just a simple entity class

    private final String fullName;
    private final String shortName;
    private final double smallShipmentPrice;
    private final double mediumShipmentPrice;
    private final double largeShipmentPrice;

    public ShippingProvider(String fullName, String shortName, double smallShipmentPrice,
                            double mediumShipmentPrice, double largeShipmentPrice) {
        if (smallShipmentPrice < 0 || mediumShipmentPrice < 0 || largeShipmentPrice < 0) {
            throw new IllegalArgumentException("Prices can not be negative");
        }
        this.fullName = fullName;
        this.shortName = shortName;
        this.smallShipmentPrice = smallShipmentPrice;
        this.mediumShipmentPrice = mediumShipmentPrice;
        this.largeShipmentPrice = largeShipmentPrice;
    }

    public String getFullName() {
        return fullName;
    }
    public String getShortName() {
        return shortName;
    }

    public double getShipmentPrice(ShipmentSize size) {
        // moved
        double price = -1;
        switch (size) {
            case SMALL -> price = smallShipmentPrice;
            case MEDIUM -> price = mediumShipmentPrice;
            case LARGE -> price = largeShipmentPrice;
        }
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShippingProvider that = (ShippingProvider) o;
        return Double.compare(smallShipmentPrice, that.smallShipmentPrice) == 0 &&
                Double.compare(mediumShipmentPrice, that.mediumShipmentPrice) == 0 &&
                Double.compare(largeShipmentPrice, that.largeShipmentPrice) == 0 &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(shortName, that.shortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, shortName, smallShipmentPrice, mediumShipmentPrice, largeShipmentPrice);
    }
}
