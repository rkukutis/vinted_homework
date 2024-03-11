package lt.vinted.rule;

import lt.vinted.persistence.FakeDatabase;
import lt.vinted.enumerated.ShipmentSize;
import lt.vinted.entity.Transaction;
import lt.vinted.entity.ShippingProvider;

import java.time.LocalDate;
import java.util.List;

public class NthFreeRule implements Rule{

    private final FakeDatabase database;
    // this variable says which shipment is free (0 - first, 1 - second, 2 - third and so on)
    private final int freeShipmentIndex;
    private final ShipmentSize size;
    private final ShippingProvider provider;

    public NthFreeRule(FakeDatabase database) {
        this(2, ShipmentSize.LARGE, "LP", database);
    }

    public NthFreeRule(int freeShipmentIndex, ShipmentSize size, String providerShortName, FakeDatabase database) {
        if (freeShipmentIndex < 0) {
            throw new RuntimeException("Free shipment index number must not be negative");
        }
        this.freeShipmentIndex = freeShipmentIndex;
        this.size = size;
        this.provider = database.getProviderByShortName(providerShortName)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Shipping provider %s not found in DB", providerShortName)
                ));
        this.database = database;
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        // if transaction date is 2015-02-07, time period is (2025-01-30 - 2025-0-01)
        LocalDate fromDate = LocalDate.from(
                transaction.getDate().minusDays(transaction.getDate().getDayOfMonth() + 1)
        );
        LocalDate toDate = fromDate.plusMonths(1L).plusDays(1);
        // get transactions from a certain period, provider and shipment size
        // if there are only n-1 transactions, nth one is free
        List<Transaction> transactions = database.getTimePeriodTransactions(fromDate, toDate, size, provider);
        if (transactions.size() == freeShipmentIndex &&
                transaction.getSize() == size &&
                transaction.getProvider().equals(provider)) {
            transaction.setDiscount(transaction.getPrice());
            transaction.setPrice(0);
        }
        return transaction;
    }
}
