
public class Alg2 extends AlgorithmRunner {
    public static void main(String[] args) {
        try{
            for ( int i = 1; i < 11; ++i ) {
                QuantumizedOrders( 2, i );
            }
            QuantumizedOrders( 2, 100 );
        }
        finally {
            Order.cancelledFile.close();
            Order.file.close();
            Consumer.file.close();
        }
    }
    
}
