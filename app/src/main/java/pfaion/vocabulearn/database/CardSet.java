package pfaion.vocabulearn.database;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Folder.class, parentColumns = "id", childColumns = "folder"))
public class CardSet  {
    @PrimaryKey
    public int id;
    public String name;
    public int folder;
}