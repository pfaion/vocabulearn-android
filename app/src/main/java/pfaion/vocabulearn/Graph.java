package pfaion.vocabulearn;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pfaion.vocabulearn.database.Flashcard;

import static pfaion.vocabulearn.Overview.TAG;

/**
 * Created by pfaion on 11.01.18.
 */

public class Graph {

    public static void fillGraph(LineChart lineChart, Flashcard[] cards) {


        int[][] data = new int[12][10];

        for(Flashcard card : cards) {
            String h = card.history + "                ";
            for(int i = 0; i < 10; ++i) {
                String slice = h.substring(i, i+5);
                int trials = 0;
                int score = 0;
                for(int j = 0; j < 5; ++j) {
                    if(slice.charAt(j) == '0') {
                        score -= (5-j);
                        trials++;
                    } else if(slice.charAt(j) == '1') {
                        score += (5-j);
                        trials++;
                    }
                }
                if(trials == 0) {
                    data[11][i]++;
                } else {
                    int index = 5 - Math.round(score/3f);
                    data[index][i]++;
                }
            }
        }

        List<List<Entry>> allEntries = new ArrayList<>();
        for(int t = 9; t >= 0; --t) {
            float cumPercent = 0;
            for (int cat = 0; cat < data.length; ++cat) {
                if(t == 9) allEntries.add(new ArrayList<Entry>());

                float percent = 100f * data[cat][t] / cards.length;
                cumPercent += percent;

                allEntries.get(cat).add(new Entry(9 - t, cumPercent));
            }
        }
        Collections.reverse(allEntries);

        String[] labels = new String[]{
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
        };

        int[] colors = new int[] {
                Color.parseColor("#cccccc"),
                Color.parseColor("#ff0000"),
                Color.parseColor("#ff3300"),
                Color.parseColor("#ff6600"),
                Color.parseColor("#ff9900"),
                Color.parseColor("#ffcc00"),
                Color.parseColor("#ffff00"),
                Color.parseColor("#b4e000"),
                Color.parseColor("#74c200"),
                Color.parseColor("#41a300"),
                Color.parseColor("#1b8500"),
                Color.parseColor("#006600"),
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
    }

}
