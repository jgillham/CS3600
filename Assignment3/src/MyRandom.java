import java.util.Random;

class MyRandom extends Random {
    static public int MAX = 200;
    static public int MIN = 4;
    static public int K = -5;
    static public int VAR = 10;
    static public int START = 11;
    static public boolean purelyRandom = false;
    static public boolean insertLarge = false;

    private int amount = START;
    private int count = 0;
    public void newOrder() {
        int newAmt = this.amount + this.K; 
        if ( newAmt >= MIN && newAmt <= MAX ) {
            this.amount = newAmt;
        }
    }
    public int nextInt( int id, int metal, int max ) {
       ++count;
       if ( purelyRandom ) {
           return (int)111.4148809524 + (5- (int)10 * super.nextInt( max ) / max ); 
       }
       if ( insertLarge && count % 3 == 0 ) {
           return this.amount * 2;
       }

       return this.amount + VAR * super.nextInt( max ) / max; 
    }
}
