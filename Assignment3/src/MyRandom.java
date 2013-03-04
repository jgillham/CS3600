import java.util.Random;

class MyRandom extends Random {
    static public int MAX = 100;
    static public int MIN = 4;
    static public int K = -5;

    private int amount = 50;
    public void newOrder() {
        int newAmt = this.amount + this.K; 
        if ( newAmt >= MIN && newAmt <= MAX ) {
            this.amount = newAmt;
        }
    }
    public int nextInt( int id, int metal, int max ) {
       return (int)((double)this.amount * (double)super.nextInt( max )/max); 
    }
}
