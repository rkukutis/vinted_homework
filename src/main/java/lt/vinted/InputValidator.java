package lt.vinted;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

public class InputValidator {

   public static String validateTransactionString(String transactionString) {
        if (transactionString.trim().isBlank()) return null;
        String[] transactionStringParts = transactionString.trim().split(" ");
        if (!validateTimestamp(transactionStringParts[0]) || !validateSize(transactionStringParts[1]) ||
                !validateProcessor(transactionStringParts[2])) {
            return transactionString + " Ignored";
        }
        // return string unchanged if it passes validation
        return transactionString;
   }

   private static boolean validateTimestamp(String timestampString) {
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

   private static boolean validateSize(String sizeString) {
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

   private static boolean validateProcessor(String providerString) {
        // Each shipping provider entity has a short name in the database (LP or MR in this case)
       // we could call a method here that queries the DB and checks if a processor with this name exists
       if (providerString.trim().isBlank()) return false;
       Optional<ShippingProvider> providerOptional = MockDatabase.getInstance().getProviderByShortName(providerString);
       return providerOptional.isPresent();
   }
}
