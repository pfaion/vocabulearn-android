package pfaion.vocabulearn;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pfaion.vocabulearn.database.CardSet;
import pfaion.vocabulearn.database.Data;
import pfaion.vocabulearn.database.Folder;


public class FolderFragment extends Fragment {
    private static final String TAG = "Vocabulearn.FolderFragment";

    private OnFolderClickListener mListener;


    public FolderFragment() {}

    public static FolderFragment newInstance() {
        FolderFragment fragment = new FolderFragment();
        return fragment;
    }

    private Data db;
    private Folder[] array;
    private FolderRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        array = new Folder[0];
        adapter = new FolderRecyclerViewAdapter(array, mListener, getContext());
        db = Data.getInstance(getContext());

        db.getAllFolders(new Data.LoadedCb<Folder[]>() {
            @Override
            public void onSuccess(Folder[] data) {
                adapter.setArray(data);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);

        View allFolders = view.findViewById(R.id.all_folders);
        allFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAllFoldersClick();
            }
        });


        final Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAllFoldersGraphClick();
            }
        });
        db.getAllSets(new Data.LoadedCb<CardSet[]>() {
            @Override
            public void onSuccess(final CardSet[] data) {
                db.getPercentage(data, new Data.LoadedCb<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        button.setText(data + "%");
                    }
                });
            }
        });

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFolderClickListener) {
            mListener = (OnFolderClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFolderClickListener {
        // TODO: Update argument type and name
        void onFolderClick(Folder folder);
        void onFolderGraphClick(Folder folder);
        void onAllFoldersGraphClick();
        void onAllFoldersClick();
    }
}
