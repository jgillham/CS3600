import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Alg1 {
    public static void main(String[] args) {
        DescendingOrders();
        AscendingOrders();
    }
    static public void AscendingOrders() {
        try {
            SimpleDateFormat fileDate = new SimpleDateFormat(
                "MM-dd-yyyy_HH-mm-ss" );
            Order.file = new PrintWriter( new FileWriter( "Alg1-testA-orders" +
                fileDate.format( new Date( ) ) + ".csv" ) );
            Consumer.file = new PrintWriter( 
                new FileWriter( "Alg1-testA-consumers-" +
                fileDate.format( new Date( ) ) + ".csv" ) );
            Consumer.file.printf( "%s,%s,%s,%s,%s\n", 
                "Consumer",
                "Metals consumed", "Completed purchases",
                "Total waiting time (ms)", "Average waiting time" );
            for( int i = 0; i < 25; ++i ) {
                Order.file.printf( "%s,%s,%s,%s,%s,%s,%s,%s\n", 
                    IBM.metalName[0],
                    IBM.metalName[1], IBM.metalName[2], 
                    "Arrival Time (Readable)",
                    "Arrival Time (milliseconds)", "Served Time (Readable)",
                    "Served Time (milliseconds)", "Turn Around Time" );
                Project3.main( new String[]{ "1", "3", "300" } );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    static public void DescendingOrders() {
        try {
            SimpleDateFormat fileDate = new SimpleDateFormat(
                "MM-dd-yyyy_HH-mm-ss" );
            Order.file = new PrintWriter( new FileWriter( "Alg1-testD-orders" +
                fileDate.format( new Date( ) ) + ".csv" ) );
            Consumer.file = new PrintWriter( 
                new FileWriter( "Alg1-testD-consumers-" +
                fileDate.format( new Date( ) ) + ".csv" ) );
            Consumer.file.printf( "%s,%s,%s,%s,%s\n", 
                "Consumer",
                "Metals consumed", "Completed purchases",
                "Total waiting time (ms)", "Average waiting time" );
            for( int i = 0; i < 25; ++i ) {
                Order.file.printf( "%s,%s,%s,%s,%s,%s,%s,%s\n", 
                    IBM.metalName[0],
                    IBM.metalName[1], IBM.metalName[2], 
                    "Arrival Time (Readable)",
                    "Arrival Time (milliseconds)", "Served Time (Readable)",
                    "Served Time (milliseconds)", "Turn Around Time" );
                Project3.main( new String[]{ "1", "3", "300" } );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
