import java.util.Random;

/** 
 * Main program.
 */
/** The Project 3 main class. */
public class Project3 implements IBM {
    /** Array of Consumer instances. */
    private static Consumer[] consumer;

    /** Array of Consumer threads. */
    private static Thread[] cthread;

    /** The unique Broker. */
    private static Broker broker;

    /** The unique Refiner. */
    private static Refiner refiner;

    /** Startup time (used to support the time() method. */
    static public long startTime = System.currentTimeMillis();

    /** Flag to control debugging output. */
    private static boolean verbose = false;

    /** Number of consumers (from the command line). */
    private static int consumerCount;
    /** Holds the quantum for algorithm 2. */
    public static int quantum = 1;

    // Methods

    /** Get the number of consumers.
     * @return the number of consumers.
     */
    public static int consumers() {
        return consumerCount;
    } // consumers()

    /** Turn debugging output on or off.
     * @param onOff if true, turn debugging on; otherwise turn it off.
     */
    public static synchronized void setVerbose(boolean onOff) {
        verbose = onOff;
    } // setVerbose(boolean)

    /** Debug print.
     * If the debugging flag is on, print a message, preceded by the
     * name of the current thread.  If it is off, do nothing.
     * @param message the message to print.
     */
    public static void debug(Object message) {
        if (verbose)
            System.out.println(Thread.currentThread().getName()
                + ": " + message);
    } // debug(Object)

    /** Handy procedure for timing.
     * @return the elapsed time since startup, in milliseconds.
     */
    static public int time() {
        return (int)(System.currentTimeMillis() - startTime);
    }

    /** Turn a request for resources into a printable string.
     * @param req an indication of the amount of each metal desired.
     * @return a string representation of req in the format
     * <pre>"[ng+np+nu] = total"</pre> where ng, np, nu, and total are
     * integers.
     */
    public static String requestToString(int[] req) {
        StringBuffer sb = new StringBuffer();
        int sum = 0;
        for (int i = 0; i < req.length; i++) {
            sum += req[i];
            sb.append(i == 0 ? "[" : "+")
                .append(Integer.toString(req[i]));
        }
        sb.append("] = ").append(sum);
        return sb.toString();
    } // requestToString(int[])

    /** Generate an exponentially distributed random number.
     * @param rand the source of random numbers.
     * @param mean the mean of the distribution.
     * @return the next sample value.
     */
    public static int expo(Random rand, int mean) {
        return (int) Math.round(-Math.log(rand.nextDouble()) * mean);
    } // expo(double)

    /** Print a usage message and terminate. */
    private static void usage() {
        System.err.println(
            "usage: Project3 [-v] algorithm consumerCount iterations");
        System.exit(1);
    } // usage()

    /** Main program for project 3.
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        // Parse command-line arguments
        GetOpt options = new GetOpt("Project3", args, "v");
        int c;

        while ((c = options.nextOpt()) != -1) {
            switch (c) {
            default: usage();
            case 'v': verbose = true; break;
            }
        }
        if (options.optind != args.length - 4) {
            usage();
        }

        int algorithm = Integer.parseInt(args[options.optind+0]);
        consumerCount = Integer.parseInt(args[options.optind+1]);
        int iterations = Integer.parseInt(args[options.optind+2]);
        quantum = Integer.parseInt(args[options.optind+3]);

        // Create the broker
        broker = new Broker(algorithm);
        Thread tthread = new Thread(broker, "Broker");

        // Create the refiner
        refiner = new Refiner(broker, iterations);
        Thread sthread = new Thread(refiner, "Refiner");

        // Create the consumers
        consumer = new Consumer[consumerCount];
        cthread = new Thread[consumerCount];
        for (int i = 0; i < consumerCount; i++) {
            consumer[i] = new Consumer(i, broker);
            cthread[i] = new Thread(consumer[i], "Consumer " + i);
        }

        // Start the threads running
        // They all have lower priority than the main thread so none of them
        // will run until we are done starting them all.
        tthread.setPriority(Thread.NORM_PRIORITY - 1);
        tthread.start();
        sthread.setPriority(Thread.NORM_PRIORITY - 1);
        sthread.start();
        for (int i = 0; i < consumerCount; i++) {
            cthread[i].setPriority(Thread.NORM_PRIORITY - 1);
            cthread[i].start();
        }

        // Wait for all the threads to finish
        try {
            tthread.join();
            sthread.join();
            for (int i = 0; i < consumerCount; i++) {
                cthread[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } // main(String[])
} // Project3
