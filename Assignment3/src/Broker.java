
import java.util.*;

/** 
 * A centralized metals broker.
 * It receives metals from the refiner periodically and serves consumer
 * requests as soon as possible.  If it has multiple requests, it satisfies
 * them in a variety of ways, depending on the algorithm specified in the
 * constructor.
 */
public class Broker implements Runnable, IBM {

    // Parameters that determine the behavior of this broker.

    /** The number of iterations to run. */
    private int iterations;

    /** The algorithm used by this Broker. */
    private int algorithm;

    /** The number of algorithms implemented. */
    private static final int NUM_ALGORITHMS = 4;

    // Current state of this broker

    /** Flag to indicate that the Supplier has told us to shut down. */
    private boolean done = false;

    /** Queue of waiting consumers. */
    private List waiters = new ArrayList();

    /** The current stock on hand of each of the metals */
    private int[] onHand = new int[METALS];

    /** The total amount received from the Supplier */
    private int[] received = new int[METALS];

    /** Number of requests fulfilled */
    private int fulfilled = 0;

    /** The total amount delivered to Consumers */
    private int[] delivered = new int[METALS];

    /** Used to keep track of the number of Consumers who have been informed
     * that the system is shutting down.
     */
    private int shutdownCount;

    // Methods

    /** Creates a new Broker.
     * @param algorithm the algorithm used to choose among requests.
     */
    public Broker(int algorithm) {
        if (algorithm < 1 || algorithm > NUM_ALGORITHMS) {
            throw new IllegalArgumentException(
                "Algorithm must be in the range 1.." + NUM_ALGORITHMS);
        }
        this.algorithm = algorithm;
        this.iterations = iterations;
    } // Broker(int)

    // Public interface methods

    /** Accepts more resources from the supplier.
     * @param amount the number of each metal being delivered.
     */
    public synchronized void deliver(int[] amount) {
        incr(onHand, amount);
        incr(received, amount);
        notify();
    } // supply(int[])

    /** Accepts a request from a consumer and blocks the consumer until the
     * request can be satisfied.
     * @param id the requesting consumer's id (for debugging output).
     * @param amt the request.
     * @return false if the request is rejected because the system is being
     * shut down.
     */
    public boolean get(int id, int[] amt) {
        Resources.debug("get " + Resources.requestToString(amt));
        Order o = enqueue(id, amt);
        //j Null when shutting down.
        if (o == null) {
            return false;
        }
        //j Finalize the order after it has been filled.
        boolean ok = o.waitFor();
        if (ok) {
            recordFulfillment(amt);
        }
        return ok;
    } // get

    /** Tells this Broker to shut down. */
    public synchronized void shutDown() {
        Resources.debug("shutDown");
        done = true;
        notify();
    } // shutDown()

    // Other methods

    /** Creates an Order object and places it onto the waiters list.
     * @param id the requesting consumer's id (for debugging output).
     * @param amt the request.
     * @return the created Order, or null if we are shutting down (done ==
     * true).
     */
    private synchronized Order enqueue(int id, int[] amt) {
        if (done) {
            shutdownCount--;
            notify();
            return null;
        }
        //j Make a new order and add it to the waiting list.
        Order o = new Order(id, amt);
        waiters.add(o);
        notify();
        return o;
    } // enqueue(Order)

    /** Abbreviation for System.out.println.
     * @param o the object to be printed.
     */
    private static void pl(Object o) {
        System.err.println(o);
    } // pl(Object)

    /** Main loop. */
    public synchronized void run() {
        shutdownCount = Resources.consumers();
        while (!done) {
            switch (algorithm) {
            case 1: algorithm1(); break;
            case 2: algorithm2(); break;
            case 3: algorithm3(); break;
            case 4: algorithm4(); break;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Abort any pending requests from consumers
        for (Iterator i = waiters.iterator(); i.hasNext(); ) {
            Order o = (Order) i.next();
            pl("   Kill order " + o);
            i.remove();
            o.cancel(onHand);
            shutdownCount--;
        }

        // Wait for the remaining consumers to notice that the system has shut
        // down and display their results.
        while (shutdownCount > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Thread.yield();

        pl("Broker, algorithm " + algorithm);
        pl("   Purchases fulfilled:   " + fulfilled);
        pl("   Metal delivered: "
                            + Resources.requestToString(delivered));
        pl("   Metal remaining: "
                            + Resources.requestToString(onHand));
        pl("   Metal received:  "
                            + Resources.requestToString(received));
    } // run

    /** Tries to satisfy and release one or more consumers.
     * Should be called whenever conditions change.  Only called from
     * synchronized methods.
     *
     * Josh's Analysis:
     * ABOUT
     * Looks at the first order and tries to satisfy it. If the order is 
     *  satisfied then it is removed from the list. The algorithm repeats until 
     *  it cannot give any metals to the next on the list. This algorithm 
     *  resembles first-come-first-server in CPU process scheduling.
     *
     * WEAKNESSES
     * This algorithm suffers from long wait times. A larger order will cause 
     *  subsequent orders to pile behind (the convoy effect) while their wait 
     *  times skyrocket resulting in some very angry customers.
     *
     * STRENGTHS
     * This algorithm can be easier to write and understand which could result 
     *  in cheaper code maintainence. It works effectively when orders are 
     *  close to the same size. Impossible to have head dead lock due to no 
     *  priorities.
     *
     * TESTING
     * Prove that first come first served. Does it leave before its completed?
     *  Prove that other orders are served first.
     */
    private void algorithm1() {		
        while (!waiters.isEmpty()) {
            //j get the first order.
            Order o = (Order) waiters.get(0);
            //j Failed to satisfy even one order.
            if (o.give(onHand, 0) == 0) {
                return;
            }
            //j Finalize order.
            if (o.satisfied()) {
                waiters.remove(0);
                o.complete();
            }
        }
    } // algorithm1()

    /** Tries to satisfy and release one or more consumers.
     * Uses algorithm 2.
     * Should be called whenever conditions change.  Only called from
     * synchronized methods.
     *
     * Josh's Analysis:
     * ABOUT
     * Gives a maximum of 1 of each type of metal to each order starting with 
     *  the first and continues while it can give to at least 1 order. This 
     *  algorithm best compares to round-robin scheduling where the CPU gives an     *  even time slice to each process and starts back at the beginning of the 
     *  list when it reaches the end.
     *
     * WEAKNESSES
     * This algorithm suffers from a couple of weaknesses. First, if the limit 
     *  (the 2nd argument for give) is too large, this algorithm will behave 
     *  like any first-come-first-server algorithm. I don't believe the limit is
     *  too large, though. Second, if the limit is too small, the CPU time to
     *  distribute the metals could cause delays. I would never worry about this
     *  on modern hardware as only more than thousands of customers could make a
     *  difference. Third, there is no order priority so the broker could never 
     *  give special treatment to his favorite (or high paying) customers. 
     *
     * Setting the limit slightly higher could increase the performance. Also, 
     *  ensure that higher priority customers be given a larger amount of 
     *  metals.
     *
     * STRENGTHS
     * Starvation is prevented. In most cases, wait times are kept low.
     */
    private void algorithm2() {
        int amt;
        //j Continue giving until giving is not possible.
        do {
            amt = 0;
            //j Give to each order.
            for (Iterator i = waiters.iterator(); i.hasNext(); ) {
                Order o = (Order) i.next();
                //j Give returns the total amount given if any.
                amt += o.give(onHand, 1);
                //j Remove satisfied orders from the list.
                if (o.satisfied()) {
                    i.remove();
                    o.complete();
                }
            }
        } while (amt > 0);
    } // algorithm2()

    /** Tries to satisfy and release one or more consumers.
     * Uses algorithm 3.
     * Should be called whenever conditions change.  Only called from
     * synchronized methods.
     *
     * Josh's Analysis:
     * ABOUT
     * This algorithm appears to tries to satisfy the smallest order on the list
     *  and repeats while it gives any of its metals. This algorithm most 
     *  closely resembles shortest-order-first-scheduling.
     *
     * WEAKNESSES
     * Starvation maybe possible in the case where a large order is received 
     *  along with a continual stream of smaller orders. Also, wait times may
     *  be less than optimal for a pair of orders with the majority of their 
     *  orders not on the same metal. For example, lets say the an order wants
     *  4 gold and 3 platinum and another order wants 5 platinum and a 3 gold. 
     *  If the on hand metals are 3 gold and 5 platinum then server the first 
     *  order will result in higher wait times.
     *
     * STRENGTHS
     * In most cases, this algorithm reduces wait times.
     * 
     */
    private void algorithm3() {	
        while (!waiters.isEmpty()) {
            Order o;
            int min = Integer.MAX_VALUE;
            int mini = -1;
            for (int i = 0; i < waiters.size(); i++) {
                o = (Order) waiters.get(i);
                if (o.size() < min) {
                    min = o.size();
                    mini = i;
                }
            }
            o = (Order) waiters.get(mini);
            if (o.give(onHand, 0) == 0) {
                return;
            }
            if (o.satisfied()) {
                waiters.remove(mini);
                o.complete();
            }
        }
    } // algorithm3()

    /** Tries to satisfy and release one or more consumers.
     * Uses algorithm 4.
     * Should be called whenever conditions change.  Only called from
     * synchronized methods.
     *
     * Josh's analysis:
     * ABOUT
     * This algorithm is a variation of #3, but, instead we are comparing 
     *  remainging metals on the order. I can't imagine the behavior differing 
     *  from #3. This algorithm is similar to shortest-order-first-scheduling.
     *
     * WEAKNESSES
     * Similar to #3.
     *
     * STRENGTHS
     * Similar to #3.
     */
    private void algorithm4() {
        while (!waiters.isEmpty()) {
            Order o;
            //j Get the order with the fewest remaining.
            int min = Integer.MAX_VALUE;
            int mini = -1;
            for (int i = 0; i < waiters.size(); i++) {
                o = (Order) waiters.get(i);
                if (o.remaining() < min) {
                    min = o.remaining();
                    mini = i;
                }
            }
            o = (Order) waiters.get(mini);
            //j Finish when nothing was given.
            if (o.give(onHand, 0) == 0) {
                return;
            }
            //j Remove satisfied orders.
            if (o.satisfied()) {
                waiters.remove(mini);
                o.complete();
            }
        }
    } // algorithm4()

    /** Utility procedure to update an array.  a[i] += b[i] for each i.
     * @param a the array to be updated.
     * @param b the amount by which to update.
     */
    private static void incr(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    } // incr(int[],int[])

    /** Record the fact that an order was fulfilled.
     * @param amt the order.
     */
    private synchronized void recordFulfillment(int[] amt) {
        fulfilled++;
        incr(delivered, amt);
    } // recordFulfillment
} // Broker
