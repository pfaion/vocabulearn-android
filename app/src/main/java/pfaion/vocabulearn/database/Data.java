package pfaion.vocabulearn.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.stetho.common.StringUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pfaion.vocabulearn.CardViewActivity;
import pfaion.vocabulearn.CardViewActivity.ResultType;

@Database(entities = {Folder.class, CardSet.class, Flashcard.class, Result.class}, version = 7)
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

            int time = Math.round(System.currentTimeMillis() / 1000f);

            String resultString = "";
            String markedString = "";
            for (int i = 0; i < cards.length; i++) {
                if(cards[i].marked) markedString += cards[i].id + ",";
                if(results[i] != ResultType.NOT_ANSWERED) {
                    cards[i].last_trained_date = new Date(time * 1000L);
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

            sInstance.flashcardDao().updateCards(cards);

            if(resultString.length() > 0 || markedString.length() > 0) {
                if(resultString.length() > 0) resultString = resultString.substring(0, resultString.length() - 1);
                resultString += ";" + time + ";";
                if(markedString.length() > 0) resultString += markedString.substring(0, markedString.length() - 1);


                Result r = new Result();
                r.result = resultString;
                sInstance.resultDao().insert(r);



                if(!trySendingQueuedResults()) {
                    msg = "Failed to commit a response. Stored for later. Check network!";
                    return null;
                }

                msg = "Results saved!";


            } else {
                msg = "No cards trained.";
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


    public void getCardsForFolder(int folderID, LoadedCb<Flashcard[]> cb) { new GetCardsForFolderTask(folderID, cb).execute(); }
    private static class GetCardsForFolderTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<Flashcard[]> cb;
        private int folderID;
        private Flashcard[] cards;
        GetCardsForFolderTask(int folderID, LoadedCb<Flashcard[]> cb) { this.cb = cb; this.folderID = folderID; }

        @Override
        protected Void doInBackground(Void... voids) {
            cards = sInstance.flashcardDao().getFlashcardsForFolder(folderID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cb.onSuccess(cards);
        }
    }

    public void getAllCards(LoadedCb<Flashcard[]> cb) { new GetAllCardsTask(cb).execute(); }
    private static class GetAllCardsTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<Flashcard[]> cb;
        private Flashcard[] cards;
        GetAllCardsTask(LoadedCb<Flashcard[]> cb) { this.cb = cb; }

        @Override
        protected Void doInBackground(Void... voids) {
            cards = sInstance.flashcardDao().getAllCards();
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

    public void getAllSets(LoadedCb<CardSet[]> cb) {
        new GetAllSetsTask(cb).execute();
    }

    private static class GetAllSetsTask extends AsyncTask<Void, Void, Void> {

        private LoadedCb<CardSet[]> cb;
        private CardSet[] sets;
        GetAllSetsTask(LoadedCb<CardSet[]> cb) { this.cb = cb; }

        @Override
        protected Void doInBackground(Void... params) {
            sets = sInstance.cardSetDao().getAllSets();
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
                    int minLength = Math.min(5, card.history.length());
                    for(int i = 0; i < minLength; ++i) {
                        if(card.history.charAt(i) == '1') corrects++;
                    }
//                    total += Math.max(1, minLength);
                    total += 5;
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


    private static boolean trySendingQueuedResults() {
        Result[] results = sInstance.resultDao().getAllResults();
        try {
            for (Result result : results) {
                Request request = new Request.Builder()
                        .url("https://vocabulearn.herokuapp.com/API/results/" + result.result + "/")
                        .build();
                Response response = okHttp.newCall(request).execute();
                if (response.code() == 200) {
                    sInstance.resultDao().deleteResults(result);
                } else {
                    Log.d(TAG, "trySendingQueuedResults: " + result.result);
                    Log.d(TAG, "trySendingQueuedResults: INVALID RESPONSE CODE!");
                    return false;
                }
            }
        }  catch (IOException e) {
            return false;
        }
        return true;
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

                if(!trySendingQueuedResults()) {
                    messages.add("Failed to commit a previous response. Check network!");
                    return null;
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


