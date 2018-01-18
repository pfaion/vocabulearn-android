package pfaion.vocabulearn.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = CardSet.class, parentColumns = "id", childColumns = "card_set"))
public class Flashcard implements Serializable {
    @PrimaryKey
    public int id;
    public String front;
    public String back;
    public String history;
    public Date created_date;
    public Date last_trained_date;
    public int card_set;
    public boolean front_first;
    public boolean marked = false;

    public double getUrgency() {
        int score = getScore();
        long delta_time = getDeltaTimeMillis();
        double days = delta_time / 86400000.0;
        double urgency = Math.exp(-0.7*score) * days;
        return urgency;

    }

    public int getScore() {
        int correct = 0;
        int wrong = 0;
        for(int i = 0; i < 5 && i < history.length(); ++i) {
            if(history.charAt(i) == '0') wrong++;
            else if(history.charAt(i) == '1') correct++;
        }
        int score;
        if(wrong == 0 && correct == 0) score = -1;
        else score = correct - wrong;
        return score;
    }

    public long getDeltaTimeMillis() {
        return System.currentTimeMillis() - last_trained_date.getTime();
    }
}