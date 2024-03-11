package lt.vinted.utility;

import lt.vinted.persistence.FakeDatabase;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.entity.ShippingProvider;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

public class InputValidator {
    private final FakeDatabase database;

    public InputValidator(FakeDatabase database) {
        this.database = database;
    }

    public String validateTransactionString(String transactionString) {
        transactionString = transactionString.trim();
        if (transactionString.isBlank()) return transactionString + "Ignored";
        String[] transactionStringParts = {};
        if (transactionString.contains(" ")) {
            transactionStringParts = transactionString.split(" ");
        } else {
            return transactionString + " Ignored";
        }
        if (transactionStringParts.length != 3) return transactionString + " Ignored";
        if (!validateTimestamp(transactionStringParts[0]) || !validateSize(transactionStringParts[1]) ||
                !validateProcessor(transactionStringParts[2])) {
            return transactionString + " Ignored";
        }
        // return string unchanged if it passes validation
        return transactionString;
   }

   private boolean validateTimestamp(String timestampString) {
        // validate if string is valid
       if (timestampString == null || timestampString.isBlank()) {
           return false;
       }
       try{
           // validate if time is valid
           // LocalDate.parse takes leap years into account
           LocalDate time = LocalDate.parse(timestampString);
           LocalDate now = LocalDate.now();
           return time.isBefore(now);
       } catch (DateTimeException dateTimeException) {
           return false;
       }
   }

   private boolean validateSize(String sizeString) {
        if (sizeString == null || sizeString.isBlank() || sizeString.length() != 1) {
            return false;
        }
        boolean charIsValid = false;
        for (ShipmentSize size : ShipmentSize.values()) {
            if (size.sizeChar == sizeString.charAt(0)) {
                charIsValid = true;
                break;
            }
        }
        return charIsValid;
   }

   private boolean validateProcessor(String providerString) {
        // Each shipping provider entity has a short name in the database (LP or MR in this case)
       if (providerString.trim().isBlank()) return false;
       Optional<ShippingProvider> providerOptional = database.getProviderByShortName(providerString);
       return providerOptional.isPresent();
   }
}
