package pfaion.vocabulearn;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pfaion.vocabulearn.FolderFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyFolderRecyclerViewAdapter extends RecyclerView.Adapter<MyFolderRecyclerViewAdapter.ViewHolder> {

    private JSONArray array;
    private final OnListFragmentInteractionListener mListener;

    public MyFolderRecyclerViewAdapter(JSONArray arr, OnListFragmentInteractionListener listener) {
        array = arr;
        mListener = listener;
    }

    public void setArray(JSONArray arr) {
        array = arr;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        JSONObject item = null;
        try {
            item = array.getJSONObject(position);
            holder.mItem = item;
//            holder.mIdView.setText(item.getString("id"));
            holder.mContentView.setText(item.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    try {
                        mListener.onListFragmentInteraction(holder.mItem.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
//        public final TextView mIdView;
        public final TextView mContentView;
        public JSONObject mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
//            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
