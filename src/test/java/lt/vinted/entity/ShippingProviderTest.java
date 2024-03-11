package lt.vinted.entity;

import lt.vinted.enumerated.ShipmentSize;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ShippingProviderTest {
    @Test
    public void givenTwoObjectsWithSameData_thenTheyAreTheSame() {
        ShippingProvider provider1 = new ShippingProvider(
                "La Poste", "LP", 1.5, 4.9, 6.9
        );
        ShippingProvider provider2 = new ShippingProvider(
                "La Poste", "LP", 1.5, 4.9, 6.9
        );
        assertEquals(provider1, provider2);
        assertEquals(provider1.hashCode(), provider2.hashCode());
    }
    @Test
    public void givenNegativePrices_thenThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ShippingProvider(
                        "La Poste", "LP", -1, 4.9, 6.9
                )
        );
        assertThrows(IllegalArgumentException.class,
                () -> new ShippingProvider(
                        "La Poste", "LP", 1.5, -1, 6.9
                )
        );
        assertThrows(IllegalArgumentException.class,
                () -> new ShippingProvider(
                        "La Poste", "LP", 1.5, 4.9, -1
                )
        );
    }

}