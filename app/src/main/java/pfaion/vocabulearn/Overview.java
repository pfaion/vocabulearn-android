package pfaion.vocabulearn;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.util.List;

import pfaion.vocabulearn.database.CardSet;
import pfaion.vocabulearn.database.Data;
import pfaion.vocabulearn.database.Flashcard;
import pfaion.vocabulearn.database.Folder;


public class Overview extends AppCompatActivity
implements FolderFragment.OnFolderClickListener, SetFragment.OnSetClickListener {
    public static final String TAG = "Vocabulearn";

    private FrameLayout frameLayout;

    private Data db;
    private Fragment currentFragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_study:
                    currentFragment = FolderFragment.newInstance();
                    break;
                case R.id.navigation_folders:
                    currentFragment = FolderFragment.newInstance();
                    break;
                case R.id.navigation_smart_sets:
                    currentFragment = FolderFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, currentFragment);
            transaction.commit();
            return true;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(currentFragment);
        transaction.attach(currentFragment);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                db.load(new Data.LoadedCb<List<String>>() {
                    @Override
                    public void onSuccess(List<String> data) {
                        Context context = getApplicationContext();
                        String msg = "";
                        for(String s : data) {
                            msg += s + "\n";
                        }
                        if(msg.length() > 0) {
                            msg = msg.substring(0, msg.length() - 1);
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, FolderFragment.newInstance());
                        transaction.commit();
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Stetho.initializeWithDefaults(this);
        db = Data.getInstance(this);

        setContentView(R.layout.activity_overview);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        currentFragment = FolderFragment.newInstance();
        transaction.replace(R.id.frame_layout, currentFragment);
        transaction.commit();
    }


    @Override
    public void onFolderClick(Folder folder) {
        Log.d(TAG, "Clicked ID: " + folder.id);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
        currentFragment = SetFragment.newInstance(folder);
        transaction.replace(R.id.frame_layout, currentFragment);
        transaction.addToBackStack("go to folder");
        transaction.commit();

    }

    @Override
    public void onSetClick(int id) {
        Log.d(TAG, "Clicked Set: " + id);
        final Context context = this;

        db.getCardsForSet(id, new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(final Flashcard[] data) {
                SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(new SettingsDialogFragment.SettingsTransmitListener() {
                    @Override
                    public void onTransmit(Settings settings) {
                        Log.d(TAG, "start clicked");
                        Intent intent = new Intent(context, CardViewActivity.class);
                        intent.putExtra("cards", data);
                        intent.putExtra("settings", settings);
                        startActivity(intent);
                    }
                });
                dialog.show(getFragmentManager(), "SettingsDialog");
            }
        });

    }

    @Override
    public void onSetGraphClick(final CardSet set) {
        db.getCardsForSet(set.id, new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(Flashcard[] data) {
                GraphDialogFragment dialog = GraphDialogFragment.newInstance(data, set.name, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                dialog.show(getFragmentManager(), "GraphDialog");
            }
        });
    }

    @Override
    public void onFolderGraphClick(final Folder folder) {
        db.getCardsForFolder(folder.id, new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(Flashcard[] data) {
                GraphDialogFragment dialog = GraphDialogFragment.newInstance(data, folder.name, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                dialog.show(getFragmentManager(), "GraphDialog");
            }
        });
    }

    @Override
    public void onAllFoldersGraphClick() {
        db.getAllCards(new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(Flashcard[] data) {
                GraphDialogFragment dialog = GraphDialogFragment.newInstance(data, "all folders", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                dialog.show(getFragmentManager(), "GraphDialog");
            }
        });
    }

    @Override
    public void onAllFoldersClick() {
        final Context context = this;

        db.getAllCards(new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(final Flashcard[] data) {
                SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(new SettingsDialogFragment.SettingsTransmitListener() {
                    @Override
                    public void onTransmit(Settings settings) {
                        Log.d(TAG, "start clicked");
                        Intent intent = new Intent(context, CardViewActivity.class);
                        intent.putExtra("cards", data);
                        intent.putExtra("settings", settings);
                        startActivity(intent);
                    }
                });
                dialog.show(getFragmentManager(), "SettingsDialog");
            }
        });
    }

    @Override
    public void onAllSetsClick(Folder folder) {
        final Context context = this;

        db.getCardsForFolder(folder.id, new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(final Flashcard[] data) {
                SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(new SettingsDialogFragment.SettingsTransmitListener() {
                    @Override
                    public void onTransmit(Settings settings) {
                        Log.d(TAG, "start clicked");
                        Intent intent = new Intent(context, CardViewActivity.class);
                        intent.putExtra("cards", data);
                        intent.putExtra("settings", settings);
                        startActivity(intent);
                    }
                });
                dialog.show(getFragmentManager(), "SettingsDialog");
            }
        });
    }

    @Override
    public void onAllSetsGraphClick(Folder folder) {
        onFolderGraphClick(folder);
    }
}
