
/**
 * Handles requests from the customer and deliveries from the refiner.
 * 
 * @author Josh Gillham
 * @version 2-4-13
 */
public class BrokerImplementation implements Broker {
    /**
     * Creates a new class.
     * 
     * @param specialty is the code for which metal is carried.
     */
    public BrokerImplementation( int specialty ) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * A consumer calls this method to place an order. It should return only 
     * 
     * 
     * @param order a three-element array indicating the number of ounces of
     *  gold, platinum, and uranium desired. 
     */
    public void get( int[] order) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * The refiner calls this method to deliver a load of metal to the broker.
     *  The metal is the one this broker supplies.
     * 
     * @param ounces is a number of ounces.
     */
    public void deliver( int ounces ) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Another broker calls this method to swap one metal
     *  for another.
     *  
     * @param what indicates one of the metals; the other one is the
     *  metal in which this broker specializes.
     * @param ounces how many ounces to swap.
     */
    public void swap( int what, int ounces ) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * This method is used by Project2.main to audit the global state when the system
     *  shuts down. The Broker should fill in result with the amount of each metal it has
     *  on hand.
     * 
     * @param result the amount of each metal it has
     *  on hand.
     */
    public void getAmountOnHand( int[] result ) {
        throw new UnsupportedOperationException();
    }
}
