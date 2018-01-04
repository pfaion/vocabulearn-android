package pfaion.vocabulearn;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.facebook.stetho.Stetho;

import pfaion.vocabulearn.database.Data;


public class Overview extends AppCompatActivity
implements FolderFragment.OnListFragmentInteractionListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        db = Data.getInstance(this);
        db.load();

        setContentView(R.layout.activity_overview);

        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, FolderFragment.newInstance());
        transaction.commit();
    }


    @Override
    public void onListFragmentInteraction(int id) {
        Log.d("VOCABULEARN", "Clicked ID: " + id);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
        transaction.replace(R.id.frame_layout, SetFragment.newInstance(id));
        transaction.addToBackStack("go to folder");
        transaction.commit();

    }

}
