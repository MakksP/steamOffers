import java.time.LocalDate;

public class HistogramElement {
    public static final int DAYS_IN_MONTH = 30;
    public final LocalDate itemSoldDate;
    public final double price;
    public final int count;

    public HistogramElement(LocalDate date, double price, int count){
        this.itemSoldDate = date;
        this.price = price;
        this.count = count;
    }

    public boolean isWithinDateRange(LocalDate currentDate){
        LocalDate beginDate = currentDate.minusDays(DAYS_IN_MONTH);
        return itemSoldDate.isAfter(beginDate) || itemSoldDate.isEqual(beginDate);
    }
}
