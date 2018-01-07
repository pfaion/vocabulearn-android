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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pfaion.vocabulearn.CardViewActivity;
import pfaion.vocabulearn.CardViewActivity.ResultType;

@Database(entities = {Folder.class, CardSet.class, Flashcard.class, Result.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class Data extends RoomDatabase {

    private static final String TAG = "Vocabulearn.Data";

    public abstract FolderDao folderDao();
    public abstract CardSetDao cardSetDao();
    public abstract FlashcardDao flashcardDao();
    public abstract ResultDao resultDao();


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


    public static class LoadedCb<T> {
        public void onSuccess(T data){};
        public void onFailure(T data){};
    }

    public void getAllFolders(LoadedCb<Folder[]> cb) {
        new GetFoldersTask(cb).execute();
    }

    private static class GetFoldersTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<Folder[]> cb;
        private Folder[] folders;
        GetFoldersTask(LoadedCb<Folder[]> cb) { this.cb = cb; }

        @Override
        protected Void doInBackground(Void... params) {
            folders = sInstance.folderDao().getAllFolders();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cb.onSuccess(folders);
        }
    }



    public void updateCards(Flashcard[] cards, ResultType[] results, LoadedCb<String> cb) {
        new UpdateCardsTask(cards, results, cb).execute();
    }

    private static class UpdateCardsTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<String> cb;
        private Flashcard[] cards;
        private ResultType[] results;
        private String msg;
        UpdateCardsTask(Flashcard[] cards, ResultType[] results, LoadedCb<String> cb) {
            this.cb = cb;
            this.cards = cards;
            this.results = results;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String resultString = "";
            for (int i = 0; i < cards.length; i++) {
                if(results[i] != ResultType.NOT_ANSWERED) {
                    resultString += cards[i].id + ":";
                    if (results[i] == ResultType.CORRECT) {
                        resultString += "1";
                    }
                    if (results[i] == ResultType.WRONG) {
                        resultString += "0";
                    }
                    resultString += ",";
                }
            }
            if(resultString.length() > 0) {
                sInstance.flashcardDao().updateCards(cards);

                resultString = resultString.substring(0, resultString.length() - 1);

                Result r = new Result();
                r.result = resultString;

                Request request = new Request.Builder()
                        .url("https://vocabulearn.herokuapp.com/API/results/"+ r.result + "/")
                        .build();
                Response response = null;
                try {
                    response = okHttp.newCall(request).execute();
                    if(response.code() != 200) {
                        msg = "WRONG NETWORK RESPONSE!";
                        sInstance.resultDao().insert(r);
                        return null;
                    }
                } catch (IOException e) {
                    msg = "Failed to commit response. Stored for later. Check network!";
                    sInstance.resultDao().insert(r);
                    return null;
                }

                msg = "Results saved!";


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cb.onSuccess(msg);
        }
    }




    public void getCardsForSet(int setID, LoadedCb<Flashcard[]> cb) { new GetCardsForSetTask(setID, cb).execute(); }
    private static class GetCardsForSetTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<Flashcard[]> cb;
        private int setID;
        private Flashcard[] cards;
        GetCardsForSetTask(int setID, LoadedCb<Flashcard[]> cb) { this.cb = cb; this.setID = setID; }

        @Override
        protected Void doInBackground(Void... voids) {
            cards = sInstance.flashcardDao().getFlashcardsForSet(setID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cb.onSuccess(cards);
        }
    }



    public void getSets(int folderID, LoadedCb<CardSet[]> cb) {
        new GetSetsTask(folderID, cb).execute();
    }

    private static class GetSetsTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<CardSet[]> cb;
        private int folderID;
        private CardSet[] sets;
        GetSetsTask(int folderID, LoadedCb<CardSet[]> cb) { this.cb = cb; this.folderID = folderID; }

        @Override
        protected Void doInBackground(Void... params) {
            sets = sInstance.cardSetDao().getSetsForFolder(folderID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cb.onSuccess(sets);
        }
    }





    public void getPercentage(CardSet[] sets, LoadedCb<Integer> cb) {
        new GetPercentageTask(sets, cb).execute();
    }

    private static class GetPercentageTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<Integer> cb;
        private int percentage;
        private CardSet[] sets;
        GetPercentageTask(CardSet[] sets, LoadedCb<Integer> cb) { this.cb = cb; this.sets = sets; }

        @Override
        protected Void doInBackground(Void... params) {
            int corrects = 0;
            int total = 0;
            for(CardSet set : sets) {
                Flashcard[] cards = sInstance.flashcardDao().getFlashcardsForSet(set.id);
                for(Flashcard card : cards) {
                    if(card.history.length() > 0 && card.history.charAt(0) == '1'){
                        corrects++;
                    }
                    total++;
                }
            }
            percentage = Math.round(100f*corrects/total);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cb.onSuccess(percentage);
        }
    }





    public void load(LoadedCb<List<String>> cb) { new LoadAllDataTask(cb).execute(); }

    private static class LoadAllDataTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<List<String>> cb;
        private boolean success;
        private List<String> messages;
        LoadAllDataTask(LoadedCb<List<String>> cb) {
            this.cb = cb;
            success = false;
            messages = new ArrayList<>();
        }



        @Override
        protected Void doInBackground(Void... params) {
            success = false;

            try {

                Result[] results = sInstance.resultDao().getAllResults();
                for(Result result : results) {
                    Request request = new Request.Builder()
                            .url("https://vocabulearn.herokuapp.com/API/results/"+ result.result + "/")
                            .build();
                    Response response = okHttp.newCall(request).execute();
                    if(response.code() == 200) {
                        sInstance.resultDao().deleteResults(result);
                    } else {
                        messages.add("Failed to commit a previous response. Check network!");
                        return null;
                    }
                }

                if(results.length != 0) {
                    messages.add("Committed " + results.length + " previous responses.");
                }


                String url, r;
                Request request;
                Response response;
                int start, end;
                Gson gson = new Gson();


                url ="https://vocabulearn.herokuapp.com/API/folders/";
                request = new Request.Builder().url(url).build();
                response = okHttp.newCall(request).execute();
                if(response.code() != 200) {
                    messages.add("Http request failed. Check network!");
                    return null;
                }

                r = response.body().string();
                start = 12;
                end = r.length() - 1;
                r = r.substring(start, end);
                Folder[] folders = gson.fromJson(r, Folder[].class);


                url ="https://vocabulearn.herokuapp.com/API/sets/";
                request = new Request.Builder().url(url).build();
                response = okHttp.newCall(request).execute();
                if(response.code() != 200) {
                    messages.add("Http request failed. Check network!");
                    return null;
                }

                r = response.body().string();
                start = 9;
                end = r.length() - 1;
                r = r.substring(start, end);
                CardSet[] sets = gson.fromJson(r, CardSet[].class);


                url ="https://vocabulearn.herokuapp.com/API/cards/";
                request = new Request.Builder().url(url).build();
                response = okHttp.newCall(request).execute();
                if(response.code() != 200) {
                    messages.add("Http request failed. Check network!");
                    return null;
                }

                r = response.body().string();
                start = 10;
                end = r.length() - 1;
                r = r.substring(start, end);
                Flashcard[] cards = gson.fromJson(r, Flashcard[].class);


                sInstance.flashcardDao().nuke();
                sInstance.cardSetDao().nuke();
                sInstance.folderDao().nuke();
                sInstance.folderDao().insert(folders);
                sInstance.cardSetDao().insert(sets);
                sInstance.flashcardDao().insert(cards);

                messages.add("Sync complete!");

                success = true;


            } catch (IOException e) {
                messages.add("Http request failed. Check network!");
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cb.onSuccess(messages);
        }
    }

}


