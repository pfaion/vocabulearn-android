package pfaion.vocabulearn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import pfaion.vocabulearn.database.Flashcard;
import pfaion.vocabulearn.CardViewActivity.Result;

import static pfaion.vocabulearn.Overview.TAG;

public class ResultsDialogFragment extends DialogFragment {





    private View.OnClickListener cb;
    public ResultsDialogFragment() {}

    public static ResultsDialogFragment newInstance(Result[] results, View.OnClickListener cb) {
        ResultsDialogFragment fragment = new ResultsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("results", results);
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
        View view = inflater.inflate(R.layout.results_dialog, null);

        PieChart pieChart = view.findViewById(R.id.chart);


        Result[] results = (Result[]) getArguments().getSerializable("results");
        int countCorrent = 0;
        int countWrong = 0;
        int countNotAnswered = 0;
        for(int i = 0; i < results.length; ++i) {
            switch (results[i]) {
                case CORRECT:
                    countCorrent++;
                    break;
                case WRONG:
                    countWrong++;
                    break;
                case NOT_ANSWERED:
                    countNotAnswered++;
                    break;
            }
        }

        int colorCorrect = Color.parseColor("#2ca02c");
        int colorWrong = Color.parseColor("#d62728");
        int colorNotAnswered = Color.parseColor("#7f7f7f");


        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        if(countCorrent > 0) {
            entries.add(new PieEntry(countCorrent, "Correct"));
            colors.add(colorCorrect);
        }
        if(countWrong > 0) {
            entries.add(new PieEntry(countWrong, "Wrong"));
            colors.add(colorWrong);
        }
        if(countNotAnswered > 0) {
            entries.add(new PieEntry(countNotAnswered, "Not Answered"));
            colors.add(colorNotAnswered);
        }



        PieDataSet set = new PieDataSet(entries, "Results");
        set.setColors(colors);
        set.setValueTextSize(12);
        set.setValueTextColor(Color.WHITE);
        PieData data = new PieData(set);

        pieChart.setData(data);
        pieChart.setTouchEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("RESULTS");
        pieChart.setCenterTextTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setTransparentCircleColor(getResources().getColor(R.color.primaryDarkColor, null));
        pieChart.setHoleColor(getResources().getColor(R.color.primaryDarkColor, null));
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();

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