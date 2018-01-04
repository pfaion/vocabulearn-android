package pfaion.vocabulearn.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Folder  {
    @PrimaryKey
    public int id;
    public String name;
}