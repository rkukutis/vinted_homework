package lt.vinted.rule;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.persistence.FakeDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class NthFreeRuleTest {

    @Mock
    private FakeDatabase fakeDatabase;

    @BeforeEach
    public void  setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenInvalidProvider_thenThrowException() {
        Assertions.assertThrows(RuntimeException.class, () -> new NthFreeRule(
                3, ShipmentSize.SMALL, "LP", fakeDatabase)
        );
    }

    @Test
    public void givenInvalidFreeShipmentIndex_thenThrowException() {
        Assertions.assertThrows(RuntimeException.class, () -> new NthFreeRule(
                -1, ShipmentSize.SMALL, "LP", fakeDatabase)
        );
    }

    @ParameterizedTest
    @EnumSource(ShipmentSize.class)
    public void givenTransaction_ifItsNotTheThirdSmallOne_thenReturnUnchanged(ShipmentSize size) {
        ShippingProvider provider = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);

        Transaction firstTransaction = new Transaction(
                LocalDate.parse("2024-01-01"), size, provider, provider.getShipmentPrice(size), 0);

        Mockito.when(fakeDatabase.getProviderByShortName("LP")).thenReturn(Optional.of(provider));
        Mockito.when(fakeDatabase.getTimePeriodTransactions(
                        Mockito.any(LocalDate.class),
                        Mockito.any(LocalDate.class),
                        Mockito.any(ShipmentSize.class),
                        Mockito.any(ShippingProvider.class)))
                .thenReturn(List.of(firstTransaction));

        Rule nthFreeRule = new NthFreeRule(2, size, provider.getShortName(), fakeDatabase);

        Transaction secondTransaction = new Transaction(
                LocalDate.parse("2024-01-03"), size, provider, provider.getShipmentPrice(size), 0);

        // Only one transaction this month, second one doesn't get a discount
        Transaction processedTransaction = nthFreeRule.processTransaction(secondTransaction);
        Assertions.assertEquals(provider.getShipmentPrice(size), processedTransaction.getPrice());
        Assertions.assertEquals(0, processedTransaction.getDiscount());

        updateMockDatabase(List.of(firstTransaction), secondTransaction);

        Transaction thirdTransaction = new Transaction(
                LocalDate.parse("2024-01-06"), size, provider, provider.getShipmentPrice(size), 0);

        // 2 transactions this month, this one gets a discount
        processedTransaction = nthFreeRule.processTransaction(thirdTransaction);
        Assertions.assertEquals(0, processedTransaction.getPrice());
        Assertions.assertEquals(provider.getShipmentPrice(size), processedTransaction.getDiscount());

        updateMockDatabase(List.of(firstTransaction, secondTransaction), thirdTransaction);


        Transaction fourthTransaction = new Transaction(
                LocalDate.parse("2024-01-12"), size, provider, provider.getShipmentPrice(size), 0);

        // 3 transactions this month, 4th one doesn't get a discount
        processedTransaction = nthFreeRule.processTransaction(fourthTransaction);
        Assertions.assertEquals(provider.getShipmentPrice(size), processedTransaction.getPrice());
        Assertions.assertEquals(0, processedTransaction.getDiscount());
    }

    private void updateMockDatabase(List<Transaction> oldTransactions, Transaction newTransaction) {
        List<Transaction> updatedList = new ArrayList<>(oldTransactions);
        updatedList.add(newTransaction);
        Mockito.when(fakeDatabase.getTimePeriodTransactions(
                        Mockito.any(LocalDate.class),
                        Mockito.any(LocalDate.class),
                        Mockito.any(ShipmentSize.class),
                        Mockito.any(ShippingProvider.class)))
                .thenReturn(updatedList);
    }
}