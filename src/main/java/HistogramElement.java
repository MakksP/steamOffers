public class HistogramElement {
    public final String month;
    public final int day;
    public final int year;
    public final double price;
    public final int count;

    public HistogramElement(String month, int day, int year, double price, int count){
        this.month = month;
        this.day = day;
        this.year = year;
        this.price = price;
        this.count = count;
    }
}
