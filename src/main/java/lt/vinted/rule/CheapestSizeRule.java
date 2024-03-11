package lt.vinted.rule;

import lt.vinted.persistence.FakeDatabase;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.entity.Transaction;

public class CheapestSizeRule implements Rule {

    private final FakeDatabase database;

    private final ShipmentSize size;

    public CheapestSizeRule(FakeDatabase database) {
        this(ShipmentSize.SMALL, database);
    }

    public CheapestSizeRule(ShipmentSize size, FakeDatabase database) {
        this.size = size;
        this.database = database;
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        // a simple rule that fetches the cheapest small shipment provider from the DB
        // and sets this price for the currently processed transaction
        if (transaction.getSize() == size) {
            double cheapestPrice = database.getCheapestSizeShipmentProvider(size)
                    .getShipmentPrice(size);
            if (transaction.getPrice() > cheapestPrice) {
                double oldPrice = transaction.getPrice();
                transaction.setPrice(cheapestPrice);
                transaction.setDiscount(oldPrice - cheapestPrice);
            }
        }
        return transaction;
    }
}
