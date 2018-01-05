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

import com.facebook.stetho.Stetho;

import pfaion.vocabulearn.database.Data;


public class Overview extends AppCompatActivity
implements FolderFragment.OnFolderClickListener, SetFragment.OnSetClickListener {
    public static final String TAG = "Vocabulearn";

    private FrameLayout frameLayout;

    private Data db;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_study:
                    selectedFragment = FolderFragment.newInstance();
                    break;
                case R.id.navigation_folders:
                    selectedFragment = FolderFragment.newInstance();
                    break;
                case R.id.navigation_smart_sets:
                    selectedFragment = FolderFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }

    };


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
                db.load(new Data.LoadedCb<Void>() {
                    @Override
                    public void onSuccess(Void data) {
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

        final Context context = this;
        SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "start clicked");
                Intent intent = new Intent(context, CardViewActivity.class);
                startActivity(intent);
            }
        });
        dialog.show(getFragmentManager(), "SettingsDialog");

        Stetho.initializeWithDefaults(this);
        db = Data.getInstance(this);

        setContentView(R.layout.activity_overview);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, FolderFragment.newInstance());
        transaction.commit();
    }


    @Override
    public void onFolderClick(int id) {
        Log.d(TAG, "Clicked ID: " + id);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
        transaction.replace(R.id.frame_layout, SetFragment.newInstance(id));
        transaction.addToBackStack("go to folder");
        transaction.commit();

    }

    @Override
    public void onSetClick(int id) {
        Log.d(TAG, "Clicked Set: " + id);


        final Context context = this;
        SettingsDialogFragment dialog = SettingsDialogFragment.newInstance(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "start clicked");
                Intent intent = new Intent(context, CardViewActivity.class);
                startActivity(intent);
            }
        });
        dialog.show(getFragmentManager(), "SettingsDialog");
    }



}
