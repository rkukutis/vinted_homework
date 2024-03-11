package lt.vinted.rule;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.persistence.FakeDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import static org.mockito.Mockito.when;

class CheapestSizeRuleTest {

    @Mock
    private FakeDatabase fakeDatabase;

    @BeforeEach
    public void  setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenBaseTransaction_whenExistsCheaperSmallProvider_thenAdjustPrice() {
        Rule cheapestSizeRule = new CheapestSizeRule(fakeDatabase);
        ShippingProvider providerUPS = new ShippingProvider(
                "UPS", "UPS", 1,2,3);
        ShippingProvider providerLPS = new ShippingProvider(
                "Lietuvos paštas", "LPŠ", 2,3,4);
        Transaction transactionUPS = new Transaction(
                LocalDate.now(), ShipmentSize.SMALL, providerUPS, providerUPS.getShipmentPrice(ShipmentSize.SMALL), 0);
        Transaction transactionLPS = new Transaction(
                LocalDate.now(), ShipmentSize.SMALL, providerLPS, providerLPS.getShipmentPrice(ShipmentSize.SMALL), 0);

        when(fakeDatabase.getCheapestSizeShipmentProvider(ShipmentSize.SMALL)).thenReturn(providerUPS);

        Transaction processedTransaction = cheapestSizeRule.processTransaction(transactionUPS);
        Assertions.assertEquals(processedTransaction, transactionUPS);

        processedTransaction = cheapestSizeRule.processTransaction(transactionLPS);
        double expectedDiscount = providerLPS.getShipmentPrice(ShipmentSize.SMALL) - providerUPS
                .getShipmentPrice(ShipmentSize.SMALL);
        Assertions.assertEquals(providerUPS.getShipmentPrice(ShipmentSize.SMALL), processedTransaction.getPrice());
        Assertions.assertEquals(expectedDiscount, processedTransaction.getDiscount());
    }

    @Test
    public void givenBaseTransaction_whenExistsCheaperMediumProvider_thenAdjustPrice() {
        Rule cheapestSizeRule = new CheapestSizeRule(ShipmentSize.MEDIUM, fakeDatabase);
        ShippingProvider providerUPS = new ShippingProvider(
                "UPS", "UPS", 1,3,5);
        ShippingProvider providerLPS = new ShippingProvider(
                "Lietuvos paštas", "LPŠ", 1,2,3);
        Transaction transactionUPS = new Transaction(
                LocalDate.now(), ShipmentSize.MEDIUM, providerUPS, providerUPS.getShipmentPrice(ShipmentSize.MEDIUM), 0);
        Transaction transactionLPS = new Transaction(
                LocalDate.now(), ShipmentSize.MEDIUM, providerLPS, providerLPS.getShipmentPrice(ShipmentSize.MEDIUM), 0);

        when(fakeDatabase.getCheapestSizeShipmentProvider(ShipmentSize.MEDIUM)).thenReturn(providerLPS);

        Transaction processedTransaction = cheapestSizeRule.processTransaction(transactionLPS);
        Assertions.assertEquals(processedTransaction, transactionLPS);

        processedTransaction = cheapestSizeRule.processTransaction(transactionUPS);
        double expectedDiscount = providerUPS.getShipmentPrice(ShipmentSize.MEDIUM) - providerLPS
                .getShipmentPrice(ShipmentSize.MEDIUM);
        Assertions.assertEquals(providerLPS.getShipmentPrice(ShipmentSize.MEDIUM), processedTransaction.getPrice());
        Assertions.assertEquals(expectedDiscount, processedTransaction.getDiscount());
    }

    @Test
    public void givenBaseTransaction_whenExistsCheaperLargeProvider_thenAdjustPrice() {
        Rule cheapestSizeRule = new CheapestSizeRule(ShipmentSize.LARGE, fakeDatabase);
        ShippingProvider providerUPS = new ShippingProvider(
                "UPS", "UPS", 1,3,5);
        ShippingProvider providerLPS = new ShippingProvider(
                "Lietuvos paštas", "LPŠ", 1,2,12);
        Transaction transactionUPS = new Transaction(
                LocalDate.now(), ShipmentSize.LARGE, providerUPS, providerUPS.getShipmentPrice(ShipmentSize.LARGE), 0);
        Transaction transactionLPS = new Transaction(
                LocalDate.now(), ShipmentSize.LARGE, providerLPS, providerLPS.getShipmentPrice(ShipmentSize.LARGE), 0);

        when(fakeDatabase.getCheapestSizeShipmentProvider(ShipmentSize.LARGE)).thenReturn(providerUPS);

        Transaction processedTransaction = cheapestSizeRule.processTransaction(transactionUPS);
        Assertions.assertEquals(processedTransaction, transactionUPS);

        processedTransaction = cheapestSizeRule.processTransaction(transactionLPS);
        double expectedDiscount = providerLPS.getShipmentPrice(ShipmentSize.LARGE) - providerUPS
                .getShipmentPrice(ShipmentSize.LARGE);
        Assertions.assertEquals(providerUPS.getShipmentPrice(ShipmentSize.LARGE), processedTransaction.getPrice());
        Assertions.assertEquals(expectedDiscount, processedTransaction.getDiscount());
    }


}