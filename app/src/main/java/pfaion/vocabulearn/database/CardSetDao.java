package pfaion.vocabulearn.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface CardSetDao {

    @Insert
    public void insert(CardSet... sets);

    @Query("SELECT * FROM CardSet")
    public CardSet[] getAllSets();

    @Query("SELECT * FROM CardSet WHERE folder=:folderID ORDER BY name")
    public CardSet[] getSetsForFolder(int folderID);

    @Query("DELETE FROM CardSet")
    public void nuke();

}
