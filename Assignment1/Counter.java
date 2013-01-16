
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
    final public static int size;
    
    /**
     * Perform one computation: (1)add one to num and then (2)add num into sum.
     */
    static void count() {
        
    }
    
    /**
     * A synchronized version of count().
     */
    static void countSynched() {
        
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
        
    }
    
    /**
     * Called from run() to repeatedly call count().
     */
    void go( int limit) {
        
    }
    
    /**
     * A synchronized version of go().
     */
    void goSynched( int limit ) {
        
    }
    
    /**
     * Called by the Thread to begin computation.
     */
    public void run() {
        
    }
}
