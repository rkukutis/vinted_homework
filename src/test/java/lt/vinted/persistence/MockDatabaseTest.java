package lt.vinted.persistence;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;
import lt.vinted.enumerated.ShipmentSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockDatabaseTest {

    @Test
    public void whenFetchingCheapestProvider_thenReturnCheapestProvider() {
        FakeDatabase database = new FakeDatabase();

        ShippingProvider providerA = new ShippingProvider(
                "A", "A", 1,3,6);

        ShippingProvider providerB = new ShippingProvider(
                "B", "B", 2,2,7);

        ShippingProvider providerC = new ShippingProvider(
                "C", "C", 3,6,5);

        // not testing this method because it just calls the internal List add() method
        database.createProvider(providerA);
        database.createProvider(providerB);
        database.createProvider(providerC);

        ShippingProvider cheapestProvider1 = database.getCheapestSizeShipmentProvider(ShipmentSize.SMALL);
        assertEquals(providerA, cheapestProvider1);
        ShippingProvider cheapestProvider2 = database.getCheapestSizeShipmentProvider(ShipmentSize.MEDIUM);
        assertEquals(providerB, cheapestProvider2);
        ShippingProvider cheapestProvider3 = database.getCheapestSizeShipmentProvider(ShipmentSize.LARGE);
        System.out.println(cheapestProvider3.getShipmentPrice(ShipmentSize.LARGE));
        assertEquals(providerC, cheapestProvider3);
    }

    @Test
    public void whenFetchingTransactions_thenReturnThoseThatMeetCriteria() {
        FakeDatabase database = new FakeDatabase();
        ShippingProvider providerA = new ShippingProvider(
                "A", "A", 1,3,6);
        ShippingProvider providerB = new ShippingProvider(
                "B", "B", 4,1,12);
        LocalDate date = LocalDate.parse("2024-01-01");

        Transaction transaction1 = new Transaction(date.plusDays(0), ShipmentSize.SMALL, providerA, 2, 0);
        database.createTransaction(transaction1);
        Transaction transaction2 = new Transaction(date.plusDays(10), ShipmentSize.MEDIUM, providerA, 2, 0);
        database.createTransaction(transaction2);
        Transaction transaction3 = new Transaction(date.plusDays(12), ShipmentSize.SMALL, providerB, 2, 0);
        database.createTransaction(transaction3);
        Transaction transaction4 = new Transaction(date.plusDays(22), ShipmentSize.LARGE, providerA, 2, 0);
        database.createTransaction(transaction4);
        Transaction transaction5 = new Transaction(date.plusDays(25), ShipmentSize.SMALL, providerB, 2, 0);
        database.createTransaction(transaction5);
        Transaction transaction6 = new Transaction(date.plusDays(31), ShipmentSize.LARGE, providerA, 2, 0);
        database.createTransaction(transaction6);

        LocalDate from = LocalDate.parse("2024-01-02");
        LocalDate to = LocalDate.parse("2024-01-31");

        List<Transaction> transactions1 = database.getTimePeriodTransactions(from, to);
        assertEquals(4, transactions1.size());

        from = LocalDate.parse("2024-01-01");

        List<Transaction> transactions2 = database.getTimePeriodTransactions(from, to, ShipmentSize.SMALL, providerA);
        assertEquals(0, transactions2.size());
        List<Transaction> transactions3 = database.getTimePeriodTransactions(from, to, ShipmentSize.SMALL, providerB);
        assertEquals(2, transactions3.size());
        List<Transaction> transactions4 = database.getTimePeriodTransactions(from, to, ShipmentSize.LARGE, providerA);
        assertEquals(1, transactions4.size());
    }
}