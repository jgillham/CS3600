
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author josh
 */
public abstract class AlgorithmRunner {
    static public int alg = 1;
    static public int consumerCount = 7;
    static public int iterations = 3000;
    static public int kVal = 20;
    static public int expCount = 10;
    static public int maximum = 200;
    static public int minimum = 20;
    static public int quantum = 1;
    
    static public void RandomOrders( int alg ) {
        try {
            MyRandom.purelyRandom = true;
            MyRandom.K = kVal;
            Order.file = new PrintWriter( new FileWriter( 
                    getFileName( alg, "random", "orders" ) ) );
            Consumer.file = new PrintWriter( 
                new FileWriter( 
                    getFileName( alg, "random", "consumers" ) ) );
            printCustomersHeader();
            for( int i = 0; i < expCount; ++i ) {
                printOrdersHeader( Order.file );
                printOrdersHeader( Order.cancelledFile );
                Project3.startTime = System.currentTimeMillis();
                Project3.main( new String[]{ String.valueOf( alg ), 
                    String.valueOf( consumerCount ), 
                    String.valueOf( iterations ),
                    String.valueOf( quantum ) } );
                Order.file.flush();
                Consumer.file.flush();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    static public void AscendingOrders( int alg ) {
        try {
            MyRandom.purelyRandom = false;
            MyRandom.K = kVal;
            MyRandom.MAX = 200;
            MyRandom.MIN = 20;
            MyRandom.START = MyRandom.MIN - kVal;
            MyRandom.VAR = 4;
            Order.file = new PrintWriter( new FileWriter( 
                    getFileName( alg, "ascending", "orders" ) ) );
            Order.cancelledFile = new PrintWriter( new FileWriter( 
                    getFileName( alg, "ascendingCancelled", "orders" ) ) );
            Consumer.file = new PrintWriter( 
                new FileWriter( 
                    getFileName( alg, "ascending", "consumers" ) ) );
            printCustomersHeader();
            for( int i = 0; i < expCount; ++i ) {
                printOrdersHeader( Order.file );
                printOrdersHeader( Order.cancelledFile );
                Project3.startTime = System.currentTimeMillis();
                Project3.main( new String[]{ String.valueOf( alg ), 
                    String.valueOf( consumerCount ), 
                    String.valueOf( iterations ),
                    String.valueOf( quantum ) } );
                Order.file.flush();
                Consumer.file.flush();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    static public void DescendingOrders( int alg ) {
        try {
            MyRandom.K = -kVal;
            MyRandom.purelyRandom = false;
            MyRandom.MAX = 200;
            MyRandom.MIN = 20;
            MyRandom.START = MyRandom.MAX + kVal;
            MyRandom.VAR = 4;
            Order.file = new PrintWriter( new FileWriter( 
                    getFileName( alg, "descending", "orders" ) ) );
            Order.cancelledFile = new PrintWriter( new FileWriter( 
                    getFileName( alg, "descendingCancelled", "orders" ) ) );
            Consumer.file = new PrintWriter( 
                new FileWriter( 
                    getFileName( alg, "descending", "consumers" ) ) );
            printCustomersHeader();
            for( int i = 0; i < expCount; ++i ) {
                printOrdersHeader( Order.file );
                printOrdersHeader( Order.cancelledFile );
                Project3.startTime = System.currentTimeMillis();
                Project3.main( new String[]{ String.valueOf( alg ), 
                    String.valueOf( consumerCount ), 
                    String.valueOf( iterations ),
                    String.valueOf( quantum ) } );
                Order.file.flush();
                Consumer.file.flush();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    static public void QuantumizedOrders( int alg, int quantum ) {
        try {
            MyRandom.purelyRandom = true;
            Order.file = new PrintWriter( new FileWriter( 
                    getFileName( alg, "quantum" + quantum, "orders" ) ) );
            Order.cancelledFile = new PrintWriter( new FileWriter( 
                    getFileName( alg, "quantumCancelled" + quantum, "orders" ) ) );
            Consumer.file = new PrintWriter( 
                new FileWriter( 
                    getFileName( alg, "quantum"+ quantum, "consumers" ) ) );
            printCustomersHeader();
            for( int i = 0; i < expCount; ++i ) {
                printOrdersHeader( Order.file );
                printOrdersHeader( Order.cancelledFile );
                Project3.startTime = System.currentTimeMillis();
                Project3.main( new String[]{ String.valueOf( alg ), 
                    String.valueOf( consumerCount ), 
                    String.valueOf( iterations ),
                    String.valueOf( quantum ) } );
                Order.file.flush();
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    static public String getFileName( int alg, String name, String type ) {
        SimpleDateFormat fileDate = new SimpleDateFormat(
                "MM-dd-yyyy_HH-mm-ss" );
        String ret = "results/Alg" + alg +
                "-" + name  + "-" + type +
            fileDate.format( new Date( ) ) + ".csv";
        return ret;
    }
    static public void printCustomersHeader() {
        Consumer.file.printf( "%s,%s,%s,%s,%s,%s\n",
                "Quantum",
                "Consumer",
                "Metals consumed", "Completed purchases",
                "Total waiting time (ms)", "Average waiting time" );
    }
    static public void printOrdersHeader( PrintWriter file ) {
        file.printf( "%s,"
                + "%s,%s,%s,"
                + "%s,"
                + "%s,"
                + "%s,%s,"
                + "%s,%s,,,%s\n",
                "Arrival Time (ms)", 
                "First Give Time (ms)", 
                "Served Time (ms)", 
                IBM.metalName[0],
                IBM.metalName[1], IBM.metalName[2], "Metal Totals",
                "Turn Around Time",
                "Wait Time (ms)",
                "Burst Time (ms)",
                "Avg Wait (ms)");
    }
}
