package lt.vinted.rule;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.persistence.FakeDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class DiscountLimitRuleTest {

    @Mock
    private FakeDatabase fakeDatabase;

    @BeforeEach
    public void  setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 6, 8, 10, 16})
    public void whenLimitNotReached_thenApplyFullDiscount(int limit) {
        Rule discountLimitRule = new DiscountLimitRule(limit, fakeDatabase);
        double fakeDiscount = 0.6;

        ShippingProvider provider = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            Transaction newTransaction = new Transaction(
                    LocalDate.parse("2024-01-01").plusDays(i),
                    ShipmentSize.LARGE, provider,
                    provider.getShipmentPrice(ShipmentSize.LARGE) - fakeDiscount,
                    fakeDiscount
            );
            transactions.add(newTransaction);
        }

        Mockito.when(fakeDatabase.getTimePeriodTransactions(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(transactions);

        Transaction newTransaction = new Transaction(
                LocalDate.parse("2024-01-31"),
                ShipmentSize.LARGE, provider,
                provider.getShipmentPrice(ShipmentSize.LARGE) - fakeDiscount,
                fakeDiscount
        );

        Transaction processedTransaction = discountLimitRule.processTransaction(newTransaction);
        Assertions.assertEquals(provider.getShipmentPrice(ShipmentSize.LARGE) - fakeDiscount,
                processedTransaction.getPrice());
        Assertions.assertEquals(fakeDiscount, processedTransaction.getDiscount());
    }

    @Test
    public void whenLimitNotReached_thenApplyPartialDiscount() {
        double limit = 10;
        Rule discountLimitRule = new DiscountLimitRule(10, fakeDatabase);
        double fakeDiscount = 0.8;

        ShippingProvider provider = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            Transaction newTransaction = new Transaction(
                    LocalDate.parse("2024-01-01").plusDays(i),
                    ShipmentSize.LARGE, provider,
                    provider.getShipmentPrice(ShipmentSize.LARGE) - fakeDiscount,
                    fakeDiscount
            );
            transactions.add(newTransaction);
        }

        Mockito.when(fakeDatabase.getTimePeriodTransactions(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(transactions);

        Transaction newTransaction = new Transaction(
                LocalDate.parse("2024-01-31"),
                ShipmentSize.LARGE, provider,
                provider.getShipmentPrice(ShipmentSize.LARGE) - fakeDiscount,
                fakeDiscount
        );

        Transaction processedTransaction = discountLimitRule.processTransaction(newTransaction);
        double expectedDiscount = limit - 12 * fakeDiscount;
        double priceBeforeDiscount = processedTransaction.getProvider()
                .getShipmentPrice(processedTransaction.getSize());
        double delta = 0.000001d;
        Assertions.assertEquals(priceBeforeDiscount - expectedDiscount, processedTransaction.getPrice(), delta);
        Assertions.assertEquals(expectedDiscount, processedTransaction.getDiscount(), delta);
    }

    @Test
    public void whenLimitIsZero_ThenReturnOriginalPrice() {
        Rule discountLimitRule = new DiscountLimitRule(0, fakeDatabase);

        ShippingProvider provider = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);

        Transaction newTransaction = new Transaction(
                LocalDate.parse("2024-01-31"),
                ShipmentSize.LARGE, provider,
                provider.getShipmentPrice(ShipmentSize.LARGE) - 3,
                3
        );
        Transaction processedTransaction = discountLimitRule.processTransaction(newTransaction);
        Assertions.assertEquals(provider.getShipmentPrice(newTransaction.getSize()),
                processedTransaction.getPrice());
        Assertions.assertEquals(0, processedTransaction.getDiscount());

    }
    @Test
    public void whenLimitIsNegative_ThenThrowException() {
        Assertions.assertThrows(RuntimeException.class, () -> new DiscountLimitRule(-1, fakeDatabase));
    }


}