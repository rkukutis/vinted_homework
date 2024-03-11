package lt.vinted.utility;

import lt.vinted.entity.ShippingProvider;
import lt.vinted.persistence.FakeDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InputValidatorTest {

    @InjectMocks
    private InputValidator inputValidator;
    @Mock
    private FakeDatabase fakeDatabase;

    @BeforeEach
    public void  setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2024-02-29 S MR",
            "2024-02-22 M LP",
            "2024-02-01 L MR",
            "2024-01-30 S LP",
            "2024-01-19 M MR",
            "2024-01-11 L LP",
            "2024-01-01 S MR",
    })
    public void givenValidTransactionString_thenReturnUnchanged(String string) {
        ShippingProvider providerLP = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);
        ShippingProvider providerMR = new ShippingProvider(
                "Mondial Relay", "MR", 2,3,4);
        when(fakeDatabase.getProviderByShortName("LP")).thenReturn(Optional.of(providerLP));
        when(fakeDatabase.getProviderByShortName("MR")).thenReturn(Optional.of(providerMR));

        String output = inputValidator.validateTransactionString(string);
        assertFalse(output.endsWith("Ignored"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "  ",
            "/s",
            "%s",
            "\n",
            "\n \n \n",
            "2023-02-29 S LP",
            "2024-03-10T11:44:40+0000 S LP",
            "2024/01/05 M LP",
            "2023-01-01 XL LP",
            "2025-01-01 L UPS",
            "2023-01-01 sus"
    })
    public void givenInvalidTransactionString_thenReturnIgnoredAppended(String string) {
        ShippingProvider providerLP = new ShippingProvider(
                "La Poste", "LP", 1.5,4.9,6.9);
        ShippingProvider providerMR = new ShippingProvider(
                "Mondial Relay", "MR", 2,3,4);
        when(fakeDatabase.getProviderByShortName("LP")).thenReturn(Optional.of(providerLP));
        when(fakeDatabase.getProviderByShortName("MR")).thenReturn(Optional.of(providerMR));

        String output = inputValidator.validateTransactionString(string);
        assertTrue(output.endsWith("Ignored"));
    }


}