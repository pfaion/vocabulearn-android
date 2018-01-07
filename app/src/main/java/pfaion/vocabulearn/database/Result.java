package pfaion.vocabulearn.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Result  {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String result;
}