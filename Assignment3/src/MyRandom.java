import java.util.Random;

class MyRandom extends Random {
    static public int MAX = 200;
    static public int MIN = 4;
    static public int K = -5;
    static public int VAR = 10;

    private int amount = 100;
    public void newOrder() {
        int newAmt = this.amount + this.K; 
        if ( newAmt >= MIN && newAmt <= MAX ) {
            this.amount = newAmt;
        }
    }
    public int nextInt( int id, int metal, int max ) {
       return this.amount + 10 * super.nextInt( max ) / max; 
    }
}
