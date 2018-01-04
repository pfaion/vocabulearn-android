package pfaion.vocabulearn.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface FlashcardDao {

    @Insert
    public void insert(Flashcard... cards);

    @Query("SELECT * FROM Flashcard")
    public Flashcard[] getAllCards();

    @Query("DELETE FROM Flashcard")
    public void nuke();

}
