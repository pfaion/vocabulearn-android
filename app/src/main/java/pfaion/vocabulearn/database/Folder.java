package pfaion.vocabulearn.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Folder implements Serializable {
    @PrimaryKey
    public int id;
    public String name;
}