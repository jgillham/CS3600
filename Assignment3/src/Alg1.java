public class Alg1 extends AlgorithmRunner {
    
    
    public static void main(String[] args) {
        try{
            DescendingOrders( 1 );
            AscendingOrders( 1 );
            RandomOrders( 1 );
        }
        finally {
            Order.file.close();
            Order.cancelledFile.close();
            Consumer.file.close();
        }
    }
    
}