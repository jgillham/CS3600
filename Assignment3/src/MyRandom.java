import java.util.Random;

class MyRandom extends Random {
    static public int MAX = 100;
    static public int MIN = 4;
    static public int AMOUNT = 50; 
    static public int K = -10;

    private int amount ;
    public void MyRandom() {
        this.amount = AMOUNT;
    }
    public void newOrder() {
        int newAmt = this.amount + this.K; 
        if ( newAmt >= MIN && newAmt <= MAX ) {
            this.amount = newAmt;
        }
    }
    public int nextInt( int id, int metal, int max ) {
       return this.amount;
       //* super.nextInt( max ); 
    }
}
