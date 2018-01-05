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

    public interface DataLoadedCb {
        public void onSuccess();
    }
    public void load(DataLoadedCb cb) { new LoadAllDataTask(cb).execute(); }

    public void load() { load(new DataLoadedCb() {
        @Override
        public void onSuccess() {}
    }); }



    private static class LoadAllDataTask extends AsyncTask<Void, Void, Void> {

        private DataLoadedCb cb;
        LoadAllDataTask(DataLoadedCb cb) {
            this.cb = cb;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String url, r;
                Request request;
                Response response;
                int start, end;

                Gson gson = new Gson();

                url ="https://vocabulearn.herokuapp.com/API/folders/";
                request = new Request.Builder().url(url).build();
                response = okHttp.newCall(request).execute();

                r = response.body().string();
                start = 12;
                end = r.length() - 1;
                r = r.substring(start, end);
                Folder[] folders = gson.fromJson(r, Folder[].class);
                if(folders.length == 0) throw new RuntimeException("Got empty folder list");

                url ="https://vocabulearn.herokuapp.com/API/sets/";
                request = new Request.Builder().url(url).build();
                response = okHttp.newCall(request).execute();

                r = response.body().string();
                start = 9;
                end = r.length() - 1;
                r = r.substring(start, end);
                CardSet[] sets = gson.fromJson(r, CardSet[].class);
                if(sets.length == 0) throw new RuntimeException("Got empty sets list");

                url ="https://vocabulearn.herokuapp.com/API/cards/";
                request = new Request.Builder().url(url).build();
                response = okHttp.newCall(request).execute();

                r = response.body().string();
                start = 10;
                end = r.length() - 1;
                r = r.substring(start, end);
                Flashcard[] cards = gson.fromJson(r, Flashcard[].class);
                if(cards.length == 0) throw new RuntimeException("Got empty cards list");

                sInstance.flashcardDao().nuke();
                sInstance.cardSetDao().nuke();
                sInstance.folderDao().nuke();
                sInstance.folderDao().insert(folders);
                sInstance.cardSetDao().insert(sets);
                sInstance.flashcardDao().insert(cards);
                Log.d(TAG, "Data refresh successful!");

                cb.onSuccess();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}


