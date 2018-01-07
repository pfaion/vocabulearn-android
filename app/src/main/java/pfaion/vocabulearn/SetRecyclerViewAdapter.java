package pfaion.vocabulearn;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pfaion.vocabulearn.SetFragment.OnSetClickListener;
import pfaion.vocabulearn.database.CardSet;
import pfaion.vocabulearn.database.Data;
import pfaion.vocabulearn.database.Folder;

public class SetRecyclerViewAdapter extends RecyclerView.Adapter<SetRecyclerViewAdapter.ViewHolder> {
    public static final String TAG = "Vocabulearn.SetRecyclerViewAdapter";

    Data db;

    private CardSet[] array;
    private final OnSetClickListener mListener;

    public SetRecyclerViewAdapter(CardSet[] arr, OnSetClickListener listener, Context context) {
        db = Data.getInstance(context);
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
                .inflate(R.layout.fragment_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CardSet set = array[position];
        holder.mContentView.setText(set.name);

        db.getPercentage(new CardSet[]{set}, new Data.LoadedCb<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                Log.d(TAG, "onSuccess: " + data.toString());
                holder.mButton.setText(data + "%");
            }
        });

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
        public final Button mButton;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mButton = view.findViewById(R.id.button);
        }
    }
}
