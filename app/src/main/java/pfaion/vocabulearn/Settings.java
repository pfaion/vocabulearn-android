package pfaion.vocabulearn;

import java.io.Serializable;

/**
 * Created by pfaion on 08.01.18.
 */

public class Settings implements Serializable {

    public static final int AMOUNT_5 = 1;
    public static final int AMOUNT_10 = 2;
    public static final int AMOUNT_20 = 3;
    public static final int AMOUNT_30 = 4;
    public static final int AMOUNT_ALL = 5;

    public static final int SIDE_FRONT_FIRST = 11;
    public static final int SIDE_BACK_FIRST = 12;
    public static final int SIDE_MIXED = 13;

    public static final int ORDER_SMART = 21;
    public static final int ORDER_RANDOM = 22;
    public static final int ORDER_HARD = 23;
    public static final int ORDER_OLD = 24;
    public static final int ORDER_NEW = 25;

    public int amount;
    public int side;
    public int order;

    Settings(int amount, int side, int order) {
        this.amount = amount;
        this.side = side;
        this.order = order;
    }

}
