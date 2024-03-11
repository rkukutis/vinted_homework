package lt.vinted.utility;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.persistence.FakeDatabase;
import lt.vinted.utility.TransactionStringParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TransactionStringParserTest {


    @InjectMocks
    private TransactionStringParser transactionStringParser;
    @Mock
    private FakeDatabase fakeDatabase;

    @BeforeEach
    public void  setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenValidLPTransactionString_thenReturnLPTransactionObject() {
        ShippingProvider providerLP = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);

        when(fakeDatabase.getProviderByShortName("LP")).thenReturn(Optional.of(providerLP));

        Transaction transactionLP = transactionStringParser.parse("2024-02-19 M LP");
        assertEquals(providerLP, transactionLP.getProvider());
        assertEquals(ShipmentSize.MEDIUM, transactionLP.getSize());
        assertEquals(LocalDate.parse("2024-02-19"), transactionLP.getDate());
        assertEquals(providerLP.getShipmentPrice(transactionLP.getSize()),
                providerLP.getShipmentPrice(transactionLP.getSize()));
        assertEquals(0, transactionLP.getDiscount());
    }

    @Test
    public void givenValidUSPTransactionString_thenReturnUSPTransactionObject() {
        ShippingProvider providerUPS = new ShippingProvider(
                "UPS", "UPS", 1,2,3);
        when(fakeDatabase.getProviderByShortName("UPS")).thenReturn(Optional.of(providerUPS));

        Transaction transactionLP = transactionStringParser.parse("2024-02-19 M UPS");
        assertEquals(providerUPS, transactionLP.getProvider());
        assertEquals(ShipmentSize.MEDIUM, transactionLP.getSize());
        assertEquals(LocalDate.parse("2024-02-19"), transactionLP.getDate());
        assertEquals(providerUPS.getShipmentPrice(transactionLP.getSize()),
                providerUPS.getShipmentPrice(transactionLP.getSize()));
        assertEquals(0, transactionLP.getDiscount());
    }
}