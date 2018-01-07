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


public class SetFragment extends Fragment {
    private static final String TAG = "Vocabulearn.SetFragment";

    private OnSetClickListener mListener;
    private int folderID;

    public SetFragment() {}

    public static SetFragment newInstance(int folderID) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", folderID);

        SetFragment fragment = new SetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Data db;
    private CardSet[] array;
    private SetRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            folderID = arguments.getInt("id");
        }

        array = new CardSet[0];
        adapter = new SetRecyclerViewAdapter(array, mListener, getContext());
        db = Data.getInstance(getContext());

        db.getSets(folderID, new Data.LoadedCb<CardSet[]>() {
            @Override
            public void onSuccess(CardSet[] data) {
                adapter.setArray(data);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSetClickListener) {
            mListener = (OnSetClickListener) context;
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


    public interface OnSetClickListener {
        // TODO: Update argument type and name
        void onSetClick(int id);
    }
}
