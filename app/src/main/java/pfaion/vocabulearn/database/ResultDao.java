package pfaion.vocabulearn.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface ResultDao {

    @Insert
    public void insert(Result... results);

    @Query("SELECT * FROM Result")
    public Result[] getAllResults();

    @Delete
    public void deleteResults(Result... results);

}
