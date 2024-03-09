package lt.vinted;

import java.time.LocalDate;

public class TransactionStringParser {

    public static Transaction parse(String transactionString) {
        String[] parts = transactionString.split(" ");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid transaction string provided. Can not parse to Transaction object");
        }
        LocalDate transactionDate = LocalDate.parse(parts[0]);
        ShipmentSize size = ShipmentSize.fromOrdinal(parts[1]);
        ShippingProvider provider = MockDatabase.getInstance().getProviderByShortName(parts[2])
                .orElseThrow(() -> new RuntimeException(String.format("Shipment provider %s not found in DB", parts[2])));
        double price = provider.getShipmentPrice(size);
        double discount = 0;
        return new Transaction(transactionDate, size, provider, price, discount);
    }
}
