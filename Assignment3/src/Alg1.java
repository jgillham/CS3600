import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Alg1 {
    public static void main(String[] args) {
        try {
        SimpleDateFormat fileDate = new SimpleDateFormat(
            "MM-dd-yyyy_HH-mm-ss" );
        Order.file = new PrintWriter( new FileWriter( "results-Alg1-" +
        fileDate.format( new Date( ) ) + ".csv" ) );
        Order.file.printf( "%s,%s,%s,%s,%s,%s,%s,%s\n", IBM.metalName[0],
            IBM.metalName[1], IBM.metalName[2], "Arrival Time (Readable)",
            "Arrival Time (milliseconds)", "Served Time (Readable)",
            "Served Time (milliseconds)", "Turn Around Time" );
        Project3.main( new String[]{ "1", "3", "1000" } );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
