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

        int[][] data = new int[7][10];

        for(Flashcard card : cards) {
            String h = card.history + "                ";
            for(int i = 0; i < 10; ++i) {
                String slice = h.substring(i, i+5);
                int correct = 0;
                int wrong = 0;
                for(int j = 0; j < 5; ++j) {
                    if(slice.charAt(j) == '1') {
                        correct++;
                    } else if(slice.charAt(j) == '0') {
                        wrong++;
                    }
                }
                float total = correct + wrong;
                if(correct == 0 && wrong == 0) {
                    data[6][i]++;
                } else if(correct/total < 1f/5) {
                    data[5][i]++;
                } else if(correct/total < 2f/5) {
                    data[4][i]++;
                } else if(correct/total < 3f/5) {
                    data[3][i]++;
                } else if(correct/total < 4f/5) {
                    data[2][i]++;
                } else if(correct/total < 5f/5) {
                    data[1][i]++;
                } else if(correct/total == 1f) {
                    data[0][i]++;
                }
            }
        }

        List<List<Entry>> allEntries = new ArrayList<>();
        for(int t = 9; t >= 0; --t) {
            float cumPercent = 0;
            for (int cat = 0; cat < 7; ++cat) {
                if(t == 9) allEntries.add(new ArrayList<Entry>());

                float percent = 100f * data[cat][t] / cards.length;
                cumPercent += percent;

                allEntries.get(cat).add(new Entry(9 - t, cumPercent));
            }
        }

        String[] labels = new String[]{
                "5",
                "4",
                "3",
                "2",
                "1",
                "0",
                "N/A"
        };

        int[] colors = new int[] {
                Color.parseColor("#009900"),
                Color.parseColor("#49b800"),
                Color.parseColor("#abd600"),
                Color.parseColor("#f5c400"),
                Color.parseColor("#ff7214"),
                Color.parseColor("#ff3333"),
                Color.parseColor("#cccccc")
        };

        List<LineDataSet> dataSets = new ArrayList<>();
        for(int i = 0; i < allEntries.size(); ++i) {
            LineDataSet dataSet = new LineDataSet(allEntries.get(i), labels[i]);
            dataSet.setDrawValues(false);
            dataSet.setColor(colors[i]);
            dataSet.setFillColor(colors[i]);
            dataSet.setFillAlpha(255);
            dataSet.setDrawFilled(true);
            dataSet.setDrawCircles(false);
            dataSet.setMode(LineDataSet.Mode.LINEAR);
            dataSets.add(dataSet);
        }

        Collections.reverse(dataSets);
        LineData lineData = new LineData();
        for(LineDataSet dataSet : dataSets) {
            lineData.addDataSet(dataSet);
        }


        lineChart.setData(lineData);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        Description d = new Description();
        d.setText("");
        lineChart.setDescription(d);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getAxisLeft().setAxisMaximum(100);
        lineChart.getAxisRight().setAxisMinimum(0);
        lineChart.getAxisRight().setAxisMaximum(100);
        lineChart.setTouchEnabled(false);
        lineChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        lineChart.invalidate();




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