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


public class SetFragment extends Fragment {


    private int folderID;
    private JSONArray array;
    private MyFolderRecyclerViewAdapter adapter;

    public SetFragment() {}

    public static SetFragment newInstance(int folderID) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", folderID);

        SetFragment fragment = new SetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        array = new JSONArray();
        adapter = new MyFolderRecyclerViewAdapter(array, mListener);


        Bundle arguments = getArguments();
        if (arguments != null) {
            folderID = arguments.getInt("id");
        }

        final String TAG = "VOCABULEARN";

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url ="https://vocabulearn.herokuapp.com/API/sets/" + folderID + "/";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray newData = response.getJSONArray("sets");
//                            for (int i = 0; i < newData.length(); i++) {
//                                array.put(newData.get(i));
//                            }
                            adapter.setArray(newData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        };
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error.");
                    }
                }
        );
        queue.add(request);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);



        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return view;



    }




    // interaction stuff

//    public interface OnListFragmentInteractionListener {
//        void onListFragmentInteraction(int id);
//    }
    private FolderFragment.OnListFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FolderFragment.OnListFragmentInteractionListener) {
            mListener = (FolderFragment.OnListFragmentInteractionListener) context;
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
}
