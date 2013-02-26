/**
 * A partially-filled order for metals.  It keeps track of the amounts
 * requested and granted and serves as a waiting place for the ordering thread
 * to wait for the order to be completely filled.
 */
public class Order {
    /** Source of sequence numbers. */
    private static int nextSeq = 0;

    /** The original amount requested. */
    private int[] request;

    /** The requesting consumer's id (for debugging output). */
    private int id;

    /** The sequence number of this request in the order received. */
    public final int seq;

    /** The amount allocated thus far. */
    private int[] alloc;

    /** Indication that this request has been released by calling release(). */
    private boolean done = false;

    /** Return code to be returned by waitFor.  The value false indicates
     * the request was refused (because the system is shutting down).
     */
    private boolean result;

    /** Creates a new Order.
     * Note:  This method is not thread-safe because it access the static field
     * nextSeq, so it should be only called from a synchronized method that
     * prevents two concurrent invocations of "new Order(...)".
     * @param id the requesting consumer's id (for debugging output).
     * @param request the amount of each metal needed.
     */
    public Order(int id, int[] request) {
        this.id = id;
        this.request = (int[]) request.clone();
        this.seq = ++nextSeq;
        alloc = new int[request.length];
    } // Order(int[])

    /** Check whether the remainder of this request is less than or equal to
     * amt (in all metals).
     * @param amt the amount to compare to.
     * @return true if the request can be completely satisfied by amt.
     */
    public synchronized boolean lessOrEqual(int[] amt) {
        for (int i = 0; i < request.length; i++) {
            if (request[i] - alloc[i] > amt[i]) {
                return false;
            }
        }
        return true;
    } // lessOrEqual(int[])

    /** Gives some resources to this Order from supply.
     * The amount of each resource given is the minimum of the amount in supply
     * and the amount needed to complete the order.  However, if limit is
     * postive, no more than 'limit' units of any resource will be given.
     * @param supply the source of metals.
     * @param limit if greater than zero, do not give more than this amount of
     * any resource.
     * @return the total number of units of resources given.
     */
    public synchronized int give(int[] supply, int limit) {
        int result = 0;
        for (int i = 0; i < request.length; i++) {
            //j The difference approaches zero so we never worry about
            //j  negative amt.
            int amt = request[i] - alloc[i];
            //j Don't give more than existing supplies.
            if (amt > supply[i]) {
                amt = supply[i];
            }
            //j Don't go over the limit.
            if (limit > 0 && amt > limit) {
                amt = limit;
            }
            alloc[i] += amt;
            supply[i] -= amt;
            result += amt;
        }
        return result;
    } // give(int[],int)

    /** Checks whether this order is satified.
     * @return true if the amount allocated matches the amount requested, for
     * all resources.
     */
    public synchronized boolean satisfied() {
        for (int i = 0; i < request.length; i++) {
            if (alloc[i] != request[i]) {
                return false;
            }
        }
        return true;
    } // satisfied()

    /** Returns the total amount requested.
     * @return the sum of requests for all resources.
     */
    public synchronized int size() {
        int sum = 0;
        for (int i = 0; i < request.length; i++) {
            sum += request[i];
        }
        return sum;
    } // size()

    /** Returns the remaining amount requested.
     * @return the sum of request - alloc for all resources.
     */
    public synchronized int remaining() {
        int sum = 0;
        for (int i = 0; i < request.length; i++) {
            sum += request[i] - alloc[i];
        }
        return sum;
    } // remaining()

    /** Signals completion.
     * The consumer who placed this order is allowed to return.
     */
    public synchronized void complete() {
        result = true;
        done = true;
        notify();
    } // complete()

    /** Rejects this Order.  All resources previously granted to this Order
     * are returned to revoked, and the order is completed with a failure
     * indication.
     * @param revoked the place to put the revoked resources.
     */
    public synchronized void cancel(int[] revoked) {
        for (int i = 0; i < request.length; i++) {
            revoked[i] += alloc[i];
            alloc[i] = 0;
        }
        result = false;
        done = true;
        notify();
    } // revoke(int[])

    /** Waits for this order to be fulfilled.
     * @return false if the order was rejected because of shutdown.
     */
    public synchronized boolean waitFor() {
        while (!done) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    } // waitFor()

    /** Turns this Order into a printable string.
     * Syntax is <code>seq:consumer[alloc1/request1,...,allocn/requestn]</code>
     * where <code>seq</code> is the sequence number of this Order, 
     * <code>consumer</code> is the id of the requesting Consumer,
     * <code>alloci</code> is the amount of the ith metal already allocated
     * to this order, and <code>requesti</code> is the initial request amount
     * of metal i.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(seq + ":" + id);
        for (int i = 0; i < request.length; i++) {
            sb.append(i == 0 ? '[' : ',')
                .append(alloc[i])
                .append('/')
                .append(request[i]);
        }
        sb.append(']');
        return sb.toString();
    } // toString()

} // Order
