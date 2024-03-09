package lt.vinted.rules;

import lt.vinted.MockDatabase;
import lt.vinted.ShipmentSize;
import lt.vinted.Transaction;
import lt.vinted.ShippingProvider;

import java.time.LocalDate;
import java.util.List;

public class NthFreeRule implements Rule{

    // this variable says which shipment is free (1 - first, 2 - second, 3 - third and so on)
    private final int freeShipmentIndex;
    private ShipmentSize size;
    private final ShippingProvider provider;

    public NthFreeRule() {
        this(3, ShipmentSize.LARGE, "LP");
    }

    public NthFreeRule(int freeShipmentIndex, ShipmentSize size, String providerShortName) {
        this.freeShipmentIndex = freeShipmentIndex;
        this.size = size;
        this.provider = MockDatabase.getInstance().getProviderByShortName(providerShortName)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Shipping provider %s not found in DB", providerShortName)
                ));
    }

    @Override
    public Transaction processTransaction(Transaction transaction) {
        // if transaction date is 2015-02-07, time period is (2025-01-30 - 2025-0-01)
        LocalDate fromDate = LocalDate.from(
                transaction.getDate().minusDays(transaction.getDate().getDayOfMonth() + 1)
        );
        LocalDate toDate = fromDate.plusMonths(1L).plusDays(1);

        List<Transaction> transactions = MockDatabase.getInstance()
                .getTimePeriodTransactions(fromDate, toDate, size, provider);
        if (transactions.size() == freeShipmentIndex - 1 &&
                transaction.getSize() == size &&
                transaction.getProvider().equals(provider)) {
            transaction.setDiscount(transaction.getPrice());
            transaction.setPrice(0);
        }
        return transaction;
    }
}
