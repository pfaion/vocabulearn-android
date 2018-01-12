package pfaion.vocabulearn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pfaion.vocabulearn.CardViewActivity.ResultType;
import pfaion.vocabulearn.database.Flashcard;

import static pfaion.vocabulearn.Overview.TAG;

public class GraphDialogFragment extends DialogFragment {


    private View.OnClickListener cb;
    public GraphDialogFragment() {}

    public static GraphDialogFragment newInstance(Flashcard[] cards, String title, View.OnClickListener cb) {
        GraphDialogFragment fragment = new GraphDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("cards", cards);
        args.putString("title", title);
        fragment.setArguments(args);
        fragment.setCb(cb);
        return fragment;
    }

    private void setCb(View.OnClickListener cb) {
        this.cb = cb;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.graph_dialog, null);

        TextView title = view.findViewById(R.id.graph_title);
        title.setText("Progress on " + getArguments().getString("title"));

        LineChart lineChart = view.findViewById(R.id.chart);

        Flashcard[] cards = (Flashcard[]) getArguments().getSerializable("cards");

        Graph.fillGraph(lineChart, cards);




        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onClick(view);
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}