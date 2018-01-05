package pfaion.vocabulearn.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface FolderDao {

    @Insert
    public void insert(Folder... folders);

    @Query("SELECT * FROM Folder ORDER BY name")
    public Folder[] getAllFolders();

    @Query("DELETE FROM Folder")
    public void nuke();

}
