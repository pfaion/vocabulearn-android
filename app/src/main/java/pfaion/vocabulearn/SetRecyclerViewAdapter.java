package pfaion.vocabulearn;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pfaion.vocabulearn.SetFragment.OnSetClickListener;
import pfaion.vocabulearn.database.CardSet;
import pfaion.vocabulearn.database.Folder;

public class SetRecyclerViewAdapter extends RecyclerView.Adapter<SetRecyclerViewAdapter.ViewHolder> {

    private CardSet[] array;
    private final OnSetClickListener mListener;

    public SetRecyclerViewAdapter(CardSet[] arr, OnSetClickListener listener) {
        array = arr;
        mListener = listener;
    }

    public void setArray(CardSet[] arr) {
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
        final CardSet set = array[position];
        holder.mContentView.setText(set.name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSetClick(set.id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
        }
    }
}
