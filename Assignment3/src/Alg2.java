
public class Alg2 extends AlgorithmRunner {
    public static void main(String[] args) {
        try{
            QuantumizedOrders( 2, 1 );
            QuantumizedOrders( 2, 2 );
            QuantumizedOrders( 2, 3 );
            QuantumizedOrders( 2, 100 );
        }
        finally {
            Order.cancelledFile.close();
            Order.file.close();
            Consumer.file.close();
        }
    }
    
}
