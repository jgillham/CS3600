/**
 * Handles requests from the customer and deliveries from the refiner.
 *
 * @author Josh Gillham
 * @version 2-4-13
 */
public class BrokerImplementation implements Broker {
    /** Holds the metal index of the specialty. */
    private final int specialty;
    /** Holds the inventory of metals. */
    private int[] inventory = new int[ IBM.METALS ];
    /** Holds the inventory actively being used in a trade. */
    private int[] trading = new int[ IBM.METALS ];

    /**
     * Creates a new class.
     *
     * @param specialty is the code for which metal is carried.
     */
    public BrokerImplementation( int specialty ) {
        this.specialty = specialty;
        // Initialize inventory.
        for ( int i = 0; i < IBM.METALS; ++i ) {
            inventory[ i ] = 0;
        }
    }

    /**
     * A consumer calls this method to place an order. It should return only
     *  when the order has been filled.
     *
     * Post Conditions:
     *  -Subtracts the order from inventory.
     *  -May ask other brokers to trade.
     *
     * @param order a three-element array indicating the number of ounces of
     *  gold, platinum, and uranium desired.
     */
    public void get( int[] order) {
        /* Go through each metal in the order and move the needed inventory
         *  into the private cache (called trading). The cache protects our
         *  order from being used.
         */
        int required = 0;
        int i = 0;
        boolean threadInterrupted = false;
        try {
            for ( ; i < order.length; ++i ) {
                required += order[ i ];
                // Continue while order unmet.
                while ( !this.takeMetal( i, order[ i ] ) ) {
                    // Wait for a deliver.
                    if ( this.specialty == i ) {
                        synchronized ( this ) {
                            this.wait();
                        }
                    }
                    // Get the resources from inventory and, if the inventory
                    //  is running low, swap with the specialist.
                    else {
                        this.getNonspecialtyResource( i, order[ i ] );
                    }
                }
                synchronized ( this ) {
                    trading[ i ] += order[ i ];
                }
            }
        }
        /* This will occur when getNonspecialtyResource() is interrupted while
         *  waiting to fill an order. This is usually because Project2 is 
         *  wanting to end the program. In this case, no inventory has been 
         *  altered for the current iteration (i). Therefore, we restore 
         *  inventory for previous iterations.
         */
        catch ( StopThread e ) {
            threadInterrupted = true;
            throw new StopThread();
        }
        /* The exception will occur in the wait() or getNonspecialtyResource()
         *  functions. This is usually because Project2 is wanting to end the 
         *  program. 
         */
        catch ( InterruptedException e ) {
            threadInterrupted = true;
            throw new StopThread();
        }
        /* In the case of a thread interruption, the program needs to transfer 
         *  inventory from the cache (called trading) back to the main store. In
         *  the case of not being interruped, the program needs only to deduct 
         *  the inventory from the cache.
         *
         * No inventory has been altered for the current iteration (i). 
         *  Therefore, the program only looks at previous iterations.
         */
        finally {
            synchronized ( this ) {
                for ( --i ; i >= 0; --i ) {
                    trading[ i ] -= order[ i ];
                    if ( threadInterrupted ) {
                        this.loadMetal( i, order[ i ] );
                    }
                }
            }
        }
    }

    /**
     * The refiner calls this method to deliver a load of metal to the broker.
     *  The metal is the one this broker supplies.
     *
     * Post Conditions:
     *  -Adds the ounces into the inventory.
     *
     * @param ounces is a number of ounces.
     */
    synchronized public void deliver( int ounces ) {
        this.loadMetal( this.specialty, ounces );
        this.notifyAll();
    }

    /**
     * Another broker calls this method to swap one metal
     *  for another.
     *
     * Preconditions:
     *  -Assumes the swap requests the metal specialty.
     *
     * Post Conditions:
     *  -Changes the value of inventory.
     *
     * @param what indicates one of the metals; the other one is the
     *  metal in which this broker specializes.
     * @param ounces how many ounces to swap.
     */
    public void swap( int what, int ounces ) {
        try {
            // Continue to hold while waiting for more inventory
            //  from the refiner.
            synchronized ( this ) {
                while ( !this.takeMetal( this.specialty, ounces ) ) {
                    this.wait();
                }
                // Receive swap.
                this.loadMetal( what, ounces );
            }
            
        }
        // The exception will occur in the wait() function. Therefore,
        //  the inventory was unchanged. This usually occurs when the 
        //  main wants to end the program. 
        catch ( InterruptedException e ) {
            throw new StopThread();
        }
    }

    /**
     * This method is used by Project2.main to audit the global state when
     *  the system shuts down. The Broker should fill in result with the 
     *  amount of each metal it has on hand.
     *
     * @param result the amount of each metal it has
     *  on hand.
     */
    synchronized public void getAmountOnHand( int[] result ) {
        for ( int i = 0; i < IBM.METALS; ++i ) {
            result[ i ] = this.inventory[ i ] + this.trading[ i ];
        }
    }

    /**
     * Takes the metal from the inventory. Returns success status.
     *
     * Post Conditions:
     * -may change the value of inventory.
     *
     * @param type is the metal type.
     * @param amount is how much metal to take.
     *
     * @return TRUE if successful or FALSE otherwise.
     */
    synchronized public boolean takeMetal( int type, int amount ) {
        if ( inventory[ type ] < amount ) {
            return false;
        }
        inventory[ type ] -= amount;
        return true;
    }

    /**
     * Loads a metal onto the inventory.
     *
     * Post Conditions:
     * -Changes the value of inventory.
     *
     * @param type is the type of metal.
     * @param amount is the number of ounces.
     */
    synchronized public void loadMetal( int type, int amount ) {
        this.inventory[ type ] += amount;
    }

    /**
     * Gets the needed resources in a trade with the specialist. Does
     *  not return until successful.
     *
     * Post Conditions:
     * -may change the value of trading and inventory.
     *
     * @param what is the type of resource.
     * @param totalAmount is the ounces needed.
     */
    public void getNonspecialtyResource( int what, int totalAmount ) {
        // Take the inventory and get the amount needed if any.
        int amountNeeded = totalAmount - this.inventory[ what ];
        // Peform a swap if necessary.
        if ( amountNeeded > 0 ) {
            try {
                // Take the metals we need to offer the other broker.
                synchronized ( this ) {
                    while ( !this.takeMetal( this.specialty, amountNeeded ) ) {
                        this.wait();
                    }
                    this.trading[ this.specialty ] += amountNeeded;
                }
            }
            // The exception will occur in the wait() functions.
            //  In that case, the inventory was unchanged.
            catch ( InterruptedException e ) {
                throw new StopThread();
            }
            try {
                // Get the metals from the specialist.
                Project2.specialist( what ).swap(
                    this.specialty, amountNeeded
                );
                // Swap is complete. Load received metals.
                this.loadMetal( what, amountNeeded );

            }
            // The exception will occur in the swap() function.
            //  In that case, restore metals that were offered,
            //  but, not used.
            catch ( StopThread e ) {
                this.loadMetal( this.specialty, amountNeeded );
                throw new StopThread();
            }
            // Always remove the offered metals from the cache.
            finally {
                synchronized ( this ) {
                    this.trading[ this.specialty ] -= amountNeeded;
                }
            }

        }
    }
}
