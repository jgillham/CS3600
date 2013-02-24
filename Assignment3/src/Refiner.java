import java.util.Random;

/** 
 * A process that supplies refined metals to a broker.
 */
public class Refiner implements Runnable, IBM {
    /** The broker to supply. */
    private Broker broker;

    // Parameters that determine the behavior of this refiner.

    /** Number of times to iterate before terminating. */
    private int iterations;

    /** Mean time to sleep between iterations (in milliseconds). */
    private int meanSleepTime;

    /** Maximum amount of each metal to supply on each iteration. */
    private int[] maxDelivery = new int[METALS];

    // Misc fields

    /** Source of random numbers. */
    private Random rand;

    /** Amount supplied. */
    private int[] supplied = new int[METALS];

    // Constructors

    /** Creates a new supplier.
     * @param broker the broker to supply.
     * @param iterations the number of times to iterate before terminating.
     */
    public Refiner(Broker broker, int iterations) {
        this.broker = broker;
        this.iterations = iterations;

        // Initialize the random number generator.  If you use
        //    rand = new Random()
        // you will get a different sequence of random numbers each time you
        // run the program.
        rand = new Random(99);

        // Set values of various simulation parameters.
        // NOTE:  These numbers are designed so that average rate of production
        // exactly matches the average rate of consumption, as set in the
        // Consumer constructor:
        //
        // Consumer 0 generates an average of 1/50 order/ms with an average
        // request of 25 units/order of each metal for an average
        // consumption rate of 25/50 = 0.50 unit/ms.  Similarly, all other
        // consumers consume at an average rate of 5/50 = 0.10 unit/ms.
        // So the total average consumption rate is 0.50 + (N-1)*0.10, where
        // N is the number of consumers.
        //
        // We set up the supplier to supply 5 units for each iteration,
        // so if S is meanSleepTime and A = maxDelivery/2 is the mean amount
        // supplied, we have the equation
        //     A / S = 0.50 + (N-1)*0.10
        // or
        //     S = A / (0.50 + (N-1)*0.10)

        for (int i = 0; i < maxDelivery.length; i++) {
            maxDelivery[i] = 10;
        }

        meanSleepTime
            = (int) Math.round(5 / (0.50 + (Resources.consumers() - 1) * 0.10));

        Resources.debug(
            "Supplying an average of 5 units of each metal every "
            + meanSleepTime + " ms");
    } // Refiner(Broker,int)

    // Methods

    /** Main loop.
     * Repeatedly supply one unit of each resource to the broker.  After
     * <i>iterations</i> iterations, tell the broker to shut down.
     */
    public void run() {
        int[] amount = new int[METALS];
        for (int i = 0; i < iterations; i++) {
            try {
                int slp = Resources.expo(rand, meanSleepTime);
                Thread.sleep(slp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < amount.length; j++) {
                amount[j] = rand.nextInt(maxDelivery[j] + 1);
            }
            broker.deliver(amount);
            for (int j = 0; j < supplied.length; j++) {
                supplied[j] += amount[j];
            }
        }
        pl("Refiner");
        pl("   Iterations:     " + iterations);
        pl("   Total supplied: " + Resources.requestToString(supplied));
        broker.shutDown();
    } // run()

    /** Abbreviation for System.out.println.
     * @param o the object to be printed.
     */
    private static void pl(Object o) {
        System.err.println(o);
    } // pl(Object)
} // Refiner
