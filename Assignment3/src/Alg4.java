
public class Alg4 extends AlgorithmRunner {
    public static void main(String[] args) {
        try{
            DescendingOrders( 4 );
            AscendingOrders( 4 );
            RandomOrders( 4 );
        }
        finally {
            Order.cancelledFile.close();
            Order.file.close();
            Consumer.file.close();
        }
    }
    
}
