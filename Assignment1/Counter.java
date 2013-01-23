
/**
 * 
 * @author Josh Gillham
 * @version 1-16-13
 */
public class Counter implements java.lang.Runnable {
    /** Holds the running total. */
    public static long sum;
    /** Holds the current value to add to sum. */
    public static long num;
    /** Holds N, the number of integers to sum. */
    final public static int size = 10000;
    public static boolean runSynchronized = false;
    
    /**
     * Perform one computation: (1)add one to num and then (2)add num into sum.
     */
    static void count() {
        num = 1;
        sum += num;
    }
    
    /**
     * A synchronized version of count().
     */
    synchronized static void countSynched() {
        ++num;
    }
    
    /**
     * Initializes the Counter objects Creates the Threads Starts 
     *  the Threads Waits for all Threads to complete Prints results.
     *  
     * Outline:
     * 1. Initializes the Counter objects
     * 2. Creates the Threads
     * 3. Starts the Threads
     * 4. Waits for all Threads to complete
     * 5. Prints results
     */
    static void main( String[] args ) {
        Counter single = new Counter();
        for( int i = 0; i < 8; ++i ) {
            new Thread( new Counter() ).start();
        }
        Counter.runSynchronized = true;
        for( int i = 0; i < 8; ++i ) {
            new Thread( new Counter() ).start();
        }
    }
    
    /**
     * Called from run() to repeatedly call count().
     */
    void go( int limit) {
        for( int i = 0; i < limit; ++i ) {
            count();
        }
    }
    
    /**
     * A synchronized version of go().
     */
    synchronized void goSynched( int limit ) {
        for( int i = 0; i < limit; ++i ) {
            count();
        }
    }
    
    /**
     * Called by the Thread to begin computation.
     */
    public void run() {
        if ( !runSynchronized ) {
            go( size );
        }
        else {
            goSynched();
        }
    }
}
