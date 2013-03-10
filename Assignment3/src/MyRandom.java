import java.util.Random;

class MyRandom extends Random {
    static public int MAX = 200;
    static public int MIN = 4;
    static public int K = -5;
    static public int VAR = 10;
    static public int START = 11;
    static public boolean purelyRandom = false;

    private int amount = START;
    public void newOrder() {
        int newAmt = this.amount + this.K; 
        if ( newAmt >= MIN && newAmt <= MAX ) {
            this.amount = newAmt;
        }
    }
    public int nextInt( int id, int metal, int max ) {
       if ( purelyRandom ) {
           return super.nextInt( max );
       }
       return this.amount + VAR * super.nextInt( max ) / max; 
    }
}
