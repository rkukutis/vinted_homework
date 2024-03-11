package lt.vinted.entity;

import lt.vinted.enumerated.ShipmentSize;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    public void givenPrintMethodCall_thenPrintFormattedString() {

        LocalDate date = LocalDate.now();
        ShipmentSize size = ShipmentSize.SMALL;
        ShippingProvider provider = new ShippingProvider(
                "La Poste", "LP", 1.5, 4.9, 6.9
        );
        Transaction transaction = new Transaction(date, size, provider, provider.getShipmentPrice(size), 0);
        StringBuilder builder = new StringBuilder();
        builder
                .append(date)
                .append(" ")
                .append(size.sizeChar)
                .append(" ")
                .append(provider.getShortName())
                .append(" ")
                .append(String.format("%1.2f", transaction.getPrice()))
                .append(" ")
                .append("-");
        assertEquals(builder.toString(), transaction.toString());
    }

    @Test
    public void givenTwoObjectsWithSameData_thenTheyAreTheSame() {
        LocalDate date = LocalDate.now();
        ShipmentSize size = ShipmentSize.SMALL;
        ShippingProvider provider = new ShippingProvider(
                "La Poste", "LP", 1.5, 4.9, 6.9
        );
        Transaction transaction1 = new Transaction(date, size, provider, provider.getShipmentPrice(size), 0);
        Transaction transaction2 = new Transaction(date, size, provider, provider.getShipmentPrice(size), 0);

        assertEquals(transaction1, transaction2);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
    }

    @Test
    public void givenNegativePriceOrDiscount_thenThrowException() {
        LocalDate date = LocalDate.now();
        ShipmentSize size = ShipmentSize.SMALL;
        ShippingProvider provider = new ShippingProvider(
                "La Poste", "LP", 1.5, 4.9, 6.9
        );
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(date, size, provider, provider.getShipmentPrice(size), -1)
        );
        assertThrows(IllegalArgumentException.class,
                () -> new Transaction(date, size, provider, -1, 0)
        );
    }

}