
/**
 * 
 * @author Josh Gillham
 * @version 1-16-13
 */
public class Counter implements java.lang.Runnable {
    
    /** Holds the running total. */
    public static long sum = 0;
    /** Holds the current value to add to sum. */
    public static long num = 0;
    /** Holds N, the number of integers to sum. */
    final public static int size = 10000000;
    public static boolean runSynchronized = false;
    
    /**
     * Perform one computation: (1)add one to num and then (2)add num into sum.
     */
    void count() {
        //System.out.println( "Count" );
        num = 1;
        sum += num;
            //System.out.println( "num: " + Counter.num );
            //System.out.println( "sum: " + Counter.sum );
    }
    
    /**
     * A synchronized version of count().
     */
    void countSynched() {
        //System.out.println( "countSynched" );
        synchronized ( Counter.class ) {
            num = 1;
            sum += num;
        }
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
        final int THREADS = 2;
        Counter.num = 0;
        Counter.sum = 0;
        Counter.runSynchronized = false;
        try {
            long startTime = System.nanoTime();
            Thread thread = new Thread( new Counter() );
            thread.start();
            thread.join();
            System.out.println( "Seconds: " + ( (System.nanoTime() - startTime) ) );
            System.out.println( "num: " + Counter.num );
            System.out.println( "sum: " + Counter.sum );
            
        }
        catch ( Exception e ) {
        }
        Counter.num = 0;
        Counter.sum = 0;
        Counter.runSynchronized = false;
        try {
            long startTime = System.nanoTime();
            Thread[] array = new Thread[THREADS];
            for( int i = 0; i < THREADS; ++i ) {
                array[i] = new Thread( new Counter() );
                array[i].start();
            }
            for( int i = 0; i < THREADS; ++i ) {
                array[i].join();
            }
            System.out.println( "Seconds: " + ( (System.nanoTime() - startTime)) );
            System.out.println( "num: " + Counter.num );
            System.out.println( "sum: " + Counter.sum );
            
        }
        catch ( Exception e ) {
        }
        Counter.num = 0;
        Counter.sum = 0;
        Counter.runSynchronized = true;
        try {
            long startTime = System.nanoTime();
            Thread[] array = new Thread[THREADS];
            for( int i = 0; i < THREADS; ++i ) {
                array[i] = new Thread( new Counter() );
                array[i].start();
            }
            for( int i = 0; i < THREADS; ++i ) {
                array[i].join();
            }
            System.out.println( "Seconds: " + ( (System.nanoTime() - startTime)) );
            System.out.println( "num: " + Counter.num );
            System.out.println( "sum: " + Counter.sum );
            
        }
        catch ( Exception e ) {
        }
    }
    
    /**
     * Called from run() to repeatedly call count().
     */
    void go( int limit) {
        //System.out.println( "Count" );
        for( int i = 0; i < limit; ++i ) {
            count();
        }
    }
    
    /**
     * A synchronized version of go().
     */
    synchronized void goSynched( int limit ) {
        System.out.println( "goSynched" );
        for( int i = 0; i < limit; ++i ) {
            countSynched();
        }
    }
    
    /**
     * Called by the Thread to begin computation.
     */
    public void run() {
        //System.out.println( "Run" );
        if ( !runSynchronized ) {
            //System.out.println( "Not Synched" );
            go( size );
        }
        else {
            goSynched( size );
        }
        
    }
}
