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
import android.widget.TextView;

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
    private Folder folder;

    public SetFragment() {}

    public static SetFragment newInstance(Folder folder) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("folder", folder);

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
            folder = (Folder) arguments.getSerializable("folder");
        }

        array = new CardSet[0];
        adapter = new SetRecyclerViewAdapter(array, mListener, getContext());
        db = Data.getInstance(getContext());

        db.getSets(folder.id, new Data.LoadedCb<CardSet[]>() {
            @Override
            public void onSuccess(CardSet[] data) {
                adapter.setArray(data);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_list, container, false);

        TextView text = view.findViewById(R.id.content);
        text.setText("Folder: " + folder.name);

        View allSets = view.findViewById(R.id.all_sets);
        allSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAllSetsClick(folder);
            }
        });

        final Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAllSetsGraphClick(folder);
            }
        });
        db.getSets(folder.id, new Data.LoadedCb<CardSet[]>() {
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
        void onSetGraphClick(CardSet set);
        void onAllSetsClick(Folder folder);
        void onAllSetsGraphClick(Folder folder);
    }
}
