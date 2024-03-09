package lt.vinted.rules;

import lt.vinted.MockDatabase;
import lt.vinted.ShipmentSize;
import lt.vinted.Transaction;

public class CheapestSizeRule implements Rule {

    private final ShipmentSize size;

    public CheapestSizeRule() {
        this(ShipmentSize.SMALL);
    }

    public CheapestSizeRule(ShipmentSize size) {
        this.size = size;
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        // a simple rule that fetches the cheapest small shipment provider from the DB
        // and sets this price for the currently processed transaction
        if (transaction.getSize() == size) {
            double cheapestPrice = MockDatabase.getInstance().getCheapestSizeShipmentProvider(size)
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
