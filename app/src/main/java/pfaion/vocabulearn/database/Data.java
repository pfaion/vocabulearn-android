package pfaion.vocabulearn.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Database(entities = {Folder.class, CardSet.class, Flashcard.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class Data extends RoomDatabase {

    private static final String TAG = "Vocabulearn.Data";

    public abstract FolderDao folderDao();
    public abstract CardSetDao cardSetDao();
    public abstract FlashcardDao flashcardDao();


    private static Context context;
    private static OkHttpClient okHttp;


    private static Data sInstance;

    private static final String DATABASE_NAME = "vocabulearn_db";

    public static synchronized Data getInstance(Context context) {
        if (sInstance == null) {
            Data.context = context;
            okHttp = new OkHttpClient();
            sInstance = Room.databaseBuilder(context.getApplicationContext(), Data.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        }
        return sInstance;
    }

    public void load() {
        new LoadAllDataTask().execute();
    }

    private static class LoadAllDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            loadFolders();
            loadSets();
            loadCards();
            return null;
        }

        private void loadFolders() {
            try {
                String foldersUrl ="https://vocabulearn.herokuapp.com/API/folders/";
                Request request = new Request.Builder().url(foldersUrl).build();
                Response response = okHttp.newCall(request).execute();

                String r = response.body().string();
                int start = 12;
                int end = r.length() - 1;
                r = r.substring(start, end);
                Gson gson = new Gson();
                Folder[] folders = gson.fromJson(r, Folder[].class);

                sInstance.folderDao().nuke();
                sInstance.folderDao().insert(folders);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadSets() {
            try {
                String foldersUrl ="https://vocabulearn.herokuapp.com/API/sets/";
                Request request = new Request.Builder().url(foldersUrl).build();
                Response response = okHttp.newCall(request).execute();

                String r = response.body().string();
                int start = 9;
                int end = r.length() - 1;
                r = r.substring(start, end);
                Gson gson = new Gson();
                CardSet[] sets = gson.fromJson(r, CardSet[].class);

                sInstance.cardSetDao().nuke();
                sInstance.cardSetDao().insert(sets);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadCards() {
            try {
                String foldersUrl ="https://vocabulearn.herokuapp.com/API/cards/";
                Request request = new Request.Builder().url(foldersUrl).build();
                Response response = okHttp.newCall(request).execute();

                String r = response.body().string();
                int start = 10;
                int end = r.length() - 1;
                r = r.substring(start, end);
                Gson gson = new Gson();
                Flashcard[] cards = gson.fromJson(r, Flashcard[].class);

                sInstance.flashcardDao().nuke();
                sInstance.flashcardDao().insert(cards);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


