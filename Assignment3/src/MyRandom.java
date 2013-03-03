import java.util.Random;

class MyRandom extends Random {
    private int orderNum = 0;
    private int K = 2;
    public void newOrder() {
        ++this.orderNum;
        if ( this.K < 25 ) {
            this.K *= 2;
        }
    }
    public int nextInt( int id, int metal, int max ) {
       return this.K;
       //* super.nextInt( max ); 
    }
}
