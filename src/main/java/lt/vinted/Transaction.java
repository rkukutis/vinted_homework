package lt.vinted;

import java.time.LocalDate;

public class Transaction {
    // Another simple entity class
    private final LocalDate date;
    private final ShipmentSize size;
    private final ShippingProvider provider;

    // although BigDecimal is more appropriate for storing financial information,
    // I used double values for easier readability
    private double price;
    private double discount;

    public Transaction(LocalDate date, ShipmentSize size, ShippingProvider provider, double price, double discount) {
        this.date = date;
        this.size = size;
        this.provider = provider;
        this.price = price;
        this.discount = discount;
    }

    public LocalDate getDate() {
        return date;
    }

    public ShipmentSize getSize() {
        return size;
    }

    public ShippingProvider getProvider() {
        return provider;
    }

    public double getPrice() {
        return price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s",
                date,
                size.sizeChar,
                provider.getShortName(),
                String.format("%1.2f", price),
                discount == 0 ? "-" :  String.format("%1.2f", discount)
        );
    }
}
