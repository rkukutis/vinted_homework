package lt.vinted.utility;

import lt.vinted.persistence.FakeDatabase;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.entity.ShippingProvider;
import lt.vinted.entity.Transaction;

import java.time.LocalDate;

public class TransactionStringParser {
    private final FakeDatabase database;

    public TransactionStringParser(FakeDatabase database) {
        this.database = database;
    }

    public Transaction parse(String transactionString) {
        if (transactionString == null || transactionString.isBlank()) {
            throw new IllegalArgumentException("Input string can not be null or blank");
        }
        String[] parts = transactionString.split(" ");
        LocalDate transactionDate = LocalDate.parse(parts[0]);
        ShipmentSize size = ShipmentSize.fromSizeChar(parts[1]);
        ShippingProvider provider = database.getProviderByShortName(parts[2])
                .orElseThrow(() -> new RuntimeException(String.format("Shipment provider %s not found in DB", parts[2])));
        double price = provider.getShipmentPrice(size);
        double discount = 0;
        return new Transaction(transactionDate, size, provider, price, discount);
    }
}
