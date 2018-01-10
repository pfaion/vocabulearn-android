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
}