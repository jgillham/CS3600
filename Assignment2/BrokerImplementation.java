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

        //throw new UnsupportedOperationException();
    }

    /**
     * A consumer calls this method to place an order. It should return only
     *  when the order has been filled.
     *
     * @param order a three-element array indicating the number of ounces of
     *  gold, platinum, and uranium desired.
     */
    public void get( int[] order) {
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " - get() called" );
        for ( int l = 0; l < IBM.METALS; ++l ) {
            Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                " has " + IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                this.trading[ l ]) );
        }
        // Look for cases where inventory is too low
        //  and build up enough inventory.
        int required = 0;
        int i = 0;
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
                    // Get the resources from inventory and, if inventory
                    //  is running low swap with the specialist.
                    else {
                        this.getNonspecialtyResource( i, order[ i ] );
                    }
                }
                synchronized ( this ) {
                    trading[ i ] += order[ i ];
                }
            }
            // Send inventory.
            synchronized ( this ) {
                for ( --i ; i >= 0; --i ) {
                    trading[ i ] -= order[ i ];
                }
            }
        }
        catch ( StopThread e ) {
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - get() STOP THREAD" );
            // Reclaim inventory.
            synchronized ( this ) {
                for ( --i ; i >= 0; --i ) {
                    trading[ i ] -= order[ i ];
                    this.loadMetal( i, order[ i ] );
                }
            }
            //e.printStackTrace();
            throw new StopThread();
        }
        // The exception will occur in the wait() or getNonspecialtyResource()
        //  functions. In these cases, no inventory has been altered for the
        //  current iteration (i). Therefore, we restore inventory for
        //  previous iterations.
        catch ( InterruptedException e ) {
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - get() INTERRUPTED" );
            // Reclaim inventory.
            synchronized ( this ) {
                for ( --i ; i >= 0; --i ) {
                    trading[ i ] -= order[ i ];
                    this.loadMetal( i, order[ i ] );
                }
            }
            //e.printStackTrace();
            throw new StopThread();
        }
        catch ( Throwable e ) {
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - get() THROWABLE" );
            // Reclaim inventory.
            synchronized ( this ) {
                for ( --i ; i >= 0; --i ) {
                    trading[ i ] -= order[ i ];
                    this.loadMetal( i, order[ i ] );
                }
            }
            //e.printStackTrace();
            throw new StopThread();
        }
        for ( int l = 0; l < IBM.METALS; ++l ) {
            Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                " has " + IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                this.trading[ l ]) );
        }
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " - get() done" );
        //throw new UnsupportedOperationException();
    }

    /**
     * The refiner calls this method to deliver a load of metal to the broker.
     *  The metal is the one this broker supplies.
     *
     * @param ounces is a number of ounces.
     */
    synchronized public void deliver( int ounces ) {
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " - deliver() called" );
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " receiving " + ounces );
        this.loadMetal( this.specialty, ounces );
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " total " + this.inventory[ this.specialty ] );
        this.notifyAll();
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " - deliver() done" );
    }

    /**
     * Another broker calls this method to swap one metal
     *  for another.
     *
     * Post Condition:
     *  -Assumes the swap requests the metal specialty.
     *
     * @param what indicates one of the metals; the other one is the
     *  metal in which this broker specializes.
     * @param ounces how many ounces to swap.
     */
    public void swap( int what, int ounces ) {
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " - swap() called" );
        for ( int l = 0; l < IBM.METALS; ++l ) {
            Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                " has " + IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                this.trading[ l ]) );
        }
        assert ( what == this.specialty );
        try {
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - swap() called1" );
            // Continue to hold while waiting for more inventory
            //  from the refiner.
            synchronized ( this ) {
                while ( !this.takeMetal( this.specialty, ounces ) ) {
                    this.wait();
                    Project2.debug(IBM.metalName[ this.specialty ] + 
                        "swap done waiting");
                }
            }
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - swap() called2" );
            // Accept offering.
            this.loadMetal( what, ounces );
        }
        // The exception will occur in the wait() function.
        //  Therefore, no alteration to the inventory occured.
        catch ( InterruptedException e ) {
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - swap() STOP" );
            throw new StopThread();
        }
        for ( int l = 0; l < IBM.METALS; ++l ) {
            Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                " has " + IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                this.trading[ l ]) );
        }
        Project2.debug( IBM.metalName[ this.specialty ] + " - swap() done" );
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
            Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                " has " + IBM.metalName[ i ] + ":" + (this.inventory[ i ] + 
                this.trading[ i ]) );
            result[ i ] = this.inventory[ i ] + this.trading[ i ];
        }
    }

    /**
     * Takes the metal from the inventory. Returns success status.
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
     * Conditions:
     * -Should not be interrupted with an exception.
     *
     * @param what is the type of resource.
     * @param totalAmount is the ounces needed.
     */
    public void getNonspecialtyResource( int what, int totalAmount ) {
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " - getNonspecialtyResource() called" );
        for ( int l = 0; l < IBM.METALS; ++l ) {
            Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                " has " + 
                IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                this.trading[ l ]) );
        }
        assert ( what != this.specialty );
        // Take the inventory and get the amount needed if any.
        int amountNeeded = totalAmount - this.inventory[ what ];
        Project2.debug( IBM.metalName[ this.specialty ] + 
            " - getNonspecialtyResource() amountNeeded: " + amountNeeded );
        // Peform a swap if necessary.
        if ( amountNeeded > 0 ) {
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - getNonspecialtyResource() amountNeeded:! " );
            try {
                // Take the amount we need for the swap.
                synchronized ( this ) {
                    while ( !this.takeMetal( this.specialty, amountNeeded ) ) {
                        this.wait();
                    }
                    this.trading[ this.specialty ] += amountNeeded;
                }
            }
            // The exception will occur in the wait() functions.
            //  In that case, no inventory has been changed.
            catch ( InterruptedException e ) {
                throw new StopThread();
            }
            for ( int l = 0; l < IBM.METALS; ++l ) {
                Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                    " has " + 
                    IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                    this.trading[ l ]) );
            }
            Project2.debug( IBM.metalName[ this.specialty ] + 
                " - getNonspecialtyResource() amountNeeded:& " );
            try {
                // Get the metals from the specialist.
                Project2.specialist( what ).swap(
                    this.specialty, amountNeeded
                );
                for ( int l = 0; l < IBM.METALS; ++l ) {
                    Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                        " has " + 
                        IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                        this.trading[ l ]) );
                }
                // Swap is complete. Load received metals.
                synchronized ( this ) {
                    this.loadMetal( what, amountNeeded );
                }

            }
            // The exception will occur in the swap() function.
            //  In that case, restore metals that were taken.
            catch ( StopThread e ) {
                Project2.debug( IBM.metalName[ this.specialty ] + 
                    " - getNonspecialtyResource() STOPTHREAD" );
                // Restore specialty offered, but, not used.
                this.loadMetal( this.specialty, amountNeeded );
                throw new StopThread();
            }
            finally {
                Project2.debug( "finally" );
                synchronized ( this ) {
                    this.trading[ this.specialty ] -= amountNeeded;
                }
                for ( int l = 0; l < IBM.METALS; ++l ) {
                    Project2.debug( "At " + IBM.metalName[ this.specialty ] + 
                        " has " + 
                        IBM.metalName[ l ] + ":" + (this.inventory[ l ] + 
                        this.trading[ l ]) );
                }
            }

        }

        Project2.debug( IBM.metalName[ this.specialty ] +
            " - getNonspecialtyResource() done" );
    }
}
