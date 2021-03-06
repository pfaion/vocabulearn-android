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
    public String history_back;
    public Date created_date;
    public Date last_trained_date;
    public Date last_trained_date_back;
    public int card_set;
    public boolean front_first;
    public boolean marked = false;

    public double getUrgency() {
        double score = getScore();
        long delta_time = getDeltaTimeMillis();
        double days = delta_time / 86400000.0;
        double urgency = days / Math.pow(1.28, score);
        return urgency;
    }

    public double getUrgencyBack() {
        double score = getScoreBack();
        long delta_time = getDeltaTimeMillisBack();
        double days = delta_time / 86400000.0;
        double urgency = days / Math.pow(1.28, score);
        return urgency;
    }

    public double getScore() {
        int score = 0;
        for(int i = 0; i < 5 && i < history.length(); ++i) {
            if(history.charAt(i) == '0') score -= (5-i);
            else if(history.charAt(i) == '1') score += (5-i);
        }

        if(history.length() == 0) score = -5;
        return score;
    }

    public double getScoreBack() {
        int score = 0;
        for(int i = 0; i < 5 && i < history_back.length(); ++i) {
            if(history_back.charAt(i) == '0') score -= (5-i);
            else if(history_back.charAt(i) == '1') score += (5-i);
        }

        if(history_back.length() == 0) score = -5;
        return score;
    }

    public long getDeltaTimeMillis() {
        return System.currentTimeMillis() - last_trained_date.getTime();
    }

    public long getDeltaTimeMillisBack() {
        return System.currentTimeMillis() - last_trained_date_back.getTime();
    }
}