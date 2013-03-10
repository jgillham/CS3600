
public class Alg3 extends AlgorithmRunner {
    public static void main(String[] args) {
        try{
            expCount = 1;
            DescendingOrders( 3 );
            AscendingOrders( 3 );
            RandomOrders( 3 );
        }
        finally {
            Order.cancelledFile.close();
            Order.file.close();
            Consumer.file.close();
        }
    }
    
}
