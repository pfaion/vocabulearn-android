package pfaion.vocabulearn;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pfaion.vocabulearn.FolderFragment.OnFolderClickListener;
import pfaion.vocabulearn.database.CardSet;
import pfaion.vocabulearn.database.Data;
import pfaion.vocabulearn.database.Folder;

public class FolderRecyclerViewAdapter extends RecyclerView.Adapter<FolderRecyclerViewAdapter.ViewHolder> {

    private Data db;

    private Folder[] array;
    private final OnFolderClickListener mListener;

    public FolderRecyclerViewAdapter(Folder[] arr, OnFolderClickListener listener, Context context) {
        array = arr;
        mListener = listener;
        db = Data.getInstance(context);
    }

    public void setArray(Folder[] arr) {
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
        final Folder folder = array[position];

        db.getSets(folder.id, new Data.LoadedCb<CardSet[]>() {
            @Override
            public void onSuccess(final CardSet[] data) {
                db.getPercentage(data, new Data.LoadedCb<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        holder.mButton.setText(data + "%");
                    }
                });
            }
        });

        holder.mContentView.setText(folder.name);

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onFolderGraphClick(folder);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onFolderClick(folder.id);
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
