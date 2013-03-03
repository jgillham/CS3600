import java.util.Random;

class MyRandom extends Random {
    static private int orderNum = 0;
    static private int K = 30;
    public void newOrder() {
        ++this.orderNum;
        if ( this.K > 1 ) {
            this.K /= 2;
        }
    }
    public int nextInt( int id, int metal, int max ) {
       return this.K * super.nextInt( max ); 
    }
}
