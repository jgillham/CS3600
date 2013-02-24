/**
 * Constants and parameters of the energy trading operation.
 */
public interface IBM {
    /** Number of kinds of metal. */
    int METALS = 3;

    /** Code for gold. */
    int GOLD = 0;

    /** Code for platinum. */
    int PLATINUM = 1;

    /** Code for uranium. */
    int URANIUM = 2;

    /** Table translating metal codes to names.  For example,
     * <code>metalName[GOLD] == "gold"</code>.
     */
    String[] metalName = { "gold", "platinum", "uranium" };
} // IBM
