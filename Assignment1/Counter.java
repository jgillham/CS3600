
/**
 * 
 * @author Josh Gillham
 * @version 1-16-13
 */
public class Counter implements java.lang.Runnable {
    // BEGIN Static Section
    /** Holds the running total. */
    public static long sum = 0;
    /** Holds the current value to add to sum. */
    public static long num = 0;
    /** Holds N, the number of integers to sum. */
    final public static int size = 1000000;
    /** Holds the maximum number of threads. */
    static final int THREADS = 4;
    
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
     * 
     * @param args not used.
     */
    static void main( String[] args ) {
        try {
            Counter.num = 0;
            Counter.sum = 0;
            long startTime = System.nanoTime();
            Thread thread = new Thread( new Counter( size, false ) );
            thread.start();
            // Wait for thread to finish.
            thread.join();
            // Print report.
            System.out.println( "Single Threaded-------------------------" );
            System.out.println( "Seconds: " + ( 
                ( (double)(System.nanoTime() - startTime) ) / 1000000000
            ) );
            System.out.println( "num: " + Counter.num );
            System.out.println( "sum: " + Counter.sum );
            System.out.println( "Threads: 1" );
            System.out.println( "Single Threaded-------------------------" );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        for ( int threads = 2; threads <= THREADS; ++threads ) {
            // Unsynchronized threads.
            try {
                Counter.num = 0;
                Counter.sum = 0;
                int delagatedIterations = size / threads;
                long startTime = System.nanoTime();
                Thread[] array = new Thread[THREADS];
                // Spawn each thread.
                for ( int i = 0; i < threads; ++i ) {
                    array[i] = new Thread( 
                        new Counter( delagatedIterations, false )
                    );
                    array[i].start();
                }
                // Wait for each thread to finish.
                for ( int i = 0; i < threads; ++i ) {
                    array[i].join();
                }
                // Print out report.
                System.out.println( "Multi Threaded, Unsynchronized------" );
                System.out.println( "Seconds: " + ( 
                    ( (double)(System.nanoTime() - startTime) ) / 1000000000 
                ) );
                System.out.println( "num: " + Counter.num );
                System.out.println( "sum: " + Counter.sum );
                System.out.println( "Threads: " + threads );
                System.out.println( "Multi Threaded, Unsynchronized------" );
                
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
            // Synchronized threads.
            try {
                Counter.num = 0;
                Counter.sum = 0;
                int delagatedIterations = size / threads;
                long startTime = System.nanoTime();
                Thread[] array = new Thread[THREADS];
                // Spawn each thread.
                for ( int i = 0; i < threads; ++i ) {
                    array[i] = new Thread( 
                            new Counter( delagatedIterations, true )
                        );
                    array[i].start();
                }
                // Wait for each thread to finish.
                for ( int i = 0; i < threads; ++i ) {
                    array[i].join();
                }
                // Print out report.
                System.out.println( "Multi Threaded, Synchronized-----------" );
                System.out.println( "Seconds: " + ( 
                        ( (double)(System.nanoTime() - startTime) ) / 1000000000
                    ) );
                System.out.println( "num: " + Counter.num );
                System.out.println( "sum: " + Counter.sum );
                System.out.println( "Threads: " + threads );
                System.out.println( "Multi Threaded, Synchronized-----------" );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * A synchronized version of count().
     */
    static synchronized void countSynched() {
        num = 1;
        sum += num;
    }
    // END Static Section
    
    /** Indicates how the class should run. */
    private boolean runSynchronized = false;
    /** Indicates the number of iterations. */
    private int limit;
    
    /**
     * Initializes the class.
     * 
     * @param limit the number of iterations.
     * @param runSynchronized indicates how the class should run.
     */
    public Counter( int limit, boolean runSynchronized ) {
        this.limit = limit;
        this.runSynchronized = runSynchronized;
    }
    
    /**
     * Perform one computation: (1)add one to num and then (2)add num into sum.
     */
    void count() {
        this.num = 1;
        this.sum += this.num;
    }
    
    /**
     * Called from run() to repeatedly call count().
     */
    void go( ) {
        for ( int i = 0; i < this.limit; ++i ) {
            this.count();
        }
    }
    
    /**
     * A synchronized version of go().
     */
    synchronized void goSynched( ) {
        for ( int i = 0; i < this.limit; ++i ) {
            this.countSynched();
        }
    }
    
    /**
     * Called by the Thread to begin computation.
     */
    public void run() {
        if ( !this.runSynchronized ) {
            this.go( );
        }
        else {
            this.goSynched( );
        }
    }
}
