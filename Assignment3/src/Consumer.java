import java.util.Random;
import java.text.DecimalFormat;

/** 
 * A consumer of metals.
 */
public class Consumer implements Runnable, IBM {
    static public java.io.PrintWriter file = null;

    /** Id of this consumer */
    private int id;

    /** The broker to get metals from. */
    private Broker broker;


    // Parameters that determine the behavior of this consumer.

    /** Mean sleep time (in milliseconds) between purchase requests. */
    private int meanSleepTime;

    /** Mean amount of each quantity to purchase on each request. */
    private int[] maxPurchase = new int[METALS];

    // Current consumer state

    /** Total consumed thus far */
    private int[] consumed  = new int[METALS];

    /** Number of requests completed by this Consumer */
    private int numberOfPurchases = 0;

    /** Sum of service times for all requests */
    private int totalServiceTime = 0;

    // Misc fields

    /** A source of random numbers. */
    private MyRandom rand;

    /** A format for printing fractions to two decimal places. */
    private static DecimalFormat fmt = new DecimalFormat("0.00");

    // Constructors

    /** Create a new Consumer object.
     * @param id the unique id of this consumer.
     * @param broker the broker to contact to get stuff.
     */
    public Consumer(int id, Broker broker) {
        this.id = id;
        this.broker = broker;

        // Initialize the random number generator.  If you use
        //    rand = new Random()
        // you will get a different sequence of random numbers each time you
        // run the program.
        rand = new MyRandom( );

        // Set values of various simulation parameters.  See also the
        // Supplier constructor.

        // All consumers make purchases at the same average rate
        meanSleepTime = 50;

        // All consumers generate (roughly) balanced orders, but consumer 0 is
        // much more greedy than the others.
        for (int i = 0; i < METALS; i++) {
            maxPurchase[i] = id == 0 ? 50 : 10;
        }
        
    } // Consumer(Broker)

    // Methods

    /** Abbreviation for System.out.println.
     * @param o the object to be printed.
     */
    private static synchronized void pl(Object o) {
        System.err.println(o);
    } // pl(Object)

    /** Print information about the history of this consumer. */
    public synchronized void printReport() {
        String avg = fmt.format(totalServiceTime / (double)numberOfPurchases);
        pl("Consumer " + id
            + "\n   Metals consumed:   " + Resources.requestToString(consumed)
            + "\n   Completed purchases:    " + numberOfPurchases
            + "\n   Total waiting time(ms): " + totalServiceTime
            + "\n   Average waiting time:   " + avg);
        if ( file != null ) {
            file.printf( "%d,%d,%d,%d,%d,%s\n", Project3.quantum,
                id, (consumed[0] + consumed[1] + 
                consumed[2]), numberOfPurchases, totalServiceTime, avg );
            file.flush();
        }
    } // printReport()

    /** Main loop.
     * Repeatedly generate random orders to the broker until the broker
     * tells us to stop.  Then print some info and return.
     */
    public void run() {
        for (;;) {
            // Sleep for a while.
            int sleepTime = Resources.expo(rand, meanSleepTime);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }

            // Generate an order.  Note the hack to make sure the order is
            // non-zero.
            int[] order = new int[METALS];
            int sum;
            rand.newOrder();
            do {
                sum = 0;
                for (int i = 0; i < order.length; i++) {
                    order[i] = rand.nextInt( this.id, i, maxPurchase[i] + 1);
                    sum += order[i];
                }
            } while (sum == 0);

            int requestTime = Resources.time();
            if (!broker.get(id, order)) {
                printReport();
                return;
            }
            int  serviceTime = Resources.time() - requestTime;
            totalServiceTime += serviceTime;
            numberOfPurchases++;

            for (int i = 0; i < METALS; i++) {
                consumed[i] += order[i];
            }
        }
    } // run()
} // Consumer
