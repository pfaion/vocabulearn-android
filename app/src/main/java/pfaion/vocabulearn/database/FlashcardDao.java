package pfaion.vocabulearn.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface FlashcardDao {

    @Insert
    public void insert(Flashcard... cards);

    @Query("SELECT * FROM Flashcard")
    public Flashcard[] getAllCards();

    @Query("SELECT * FROM Flashcard WHERE card_set=:setID")
    public Flashcard[] getFlashcardsForSet(int setID);


    @Query("SELECT * FROM Flashcard INNER JOIN CardSet ON CardSet.id=Flashcard.card_set WHERE CardSet.folder=:folderID")
    public Flashcard[] getFlashcardsForFolder(int folderID);

    @Query("DELETE FROM Flashcard")
    public void nuke();

    @Update
    public void updateCards(Flashcard... cards);

}
