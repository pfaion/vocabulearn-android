package pfaion.vocabulearn;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pfaion.vocabulearn.database.Data;
import pfaion.vocabulearn.database.Flashcard;


public class StudyFragment extends Fragment {

    private OnStudyFragmentInteractionListener mListener;
    private Data db;

    public StudyFragment() {}

    public static StudyFragment newInstance() {
        StudyFragment fragment = new StudyFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_study, container, false);


        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSmartStudy();
            }
        });


        db = Data.getInstance(getContext());

        final LineChart lineChart = (LineChart)view.findViewById(R.id.chart);
        final TextView text = (TextView)view.findViewById(R.id.text);

        db.getAllCards(new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(Flashcard[] cards) {

                final double[] urgencies = new double[cards.length];
                final Integer[] scores = new Integer[cards.length];
                final int[] times = new int[cards.length];
                Integer[] indices = new Integer[cards.length];

                float max = Float.MIN_VALUE;
                float maxDelta = Float.MIN_VALUE;

                for(int i = 0; i < cards.length; ++i) {
                    urgencies[i] = cards[i].getUrgency();
                    if(urgencies[i] > max) max = (float)urgencies[i];
                    scores[i] = cards[i].getScore() + 5;
                    times[i] = Math.round(cards[i].getDeltaTimeMillis() / 1000f);
                    if(times[i] > maxDelta) maxDelta = times[i];
                    indices[i] = i;
                }

                Arrays.sort(indices, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer i1, Integer i2) {
                        return Double.compare(times[i1], times[i2]);
                    }
                });

                double[] tmpUrgencies = new double[urgencies.length];
                Integer[] tmpScores = new Integer[scores.length];
                int[] tmpTimes = new int[times.length];
                for(int i = 0; i < indices.length; ++i) {
                    tmpUrgencies[i] = urgencies[indices[i]];
                    tmpScores[i] = scores[indices[i]];
                    tmpTimes[i] = times[indices[i]];
                }

                for(int i = 0; i < indices.length; ++i) {
                    urgencies[i] = tmpUrgencies[i];
                    scores[i] = tmpScores[i];
                    times[i] = tmpTimes[i];
                }




                LineData lineData = new LineData();
                List<Entry> entries;
                LineDataSet dataSet;







//                entries = new ArrayList<>();
//                for(int i = 0; i < scores.length; ++i) {
//                    entries.add(new Entry(i, scores[i] / 11f * max));
//                }
//                dataSet = new LineDataSet(entries, "Score");
//                dataSet.setDrawCircles(false);
//                dataSet.setColor(Color.BLUE);
//
//                lineData.addDataSet(dataSet);





                int green = Color.rgb(0, 180, 0);
                entries = new ArrayList<>();
                for(int i = 0; i < times.length; ++i) {
                    entries.add(new Entry(i, times[i] / 86400f));
                }
                dataSet = new LineDataSet(entries, "Days");
                dataSet.setDrawCircles(false);
                dataSet.setColor(green);
                dataSet.setDrawFilled(true);
                dataSet.setFillColor(green);
                dataSet.setFillAlpha(50);
                dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

                lineData.addDataSet(dataSet);




                entries = new ArrayList<>();
                for(int i = 0; i < urgencies.length; ++i) {
                    entries.add(new Entry(i, (float)urgencies[i]));
                }
                dataSet = new LineDataSet(entries, "Urgency");
                dataSet.setDrawCircles(false);
                dataSet.setColor(Color.RED);
                dataSet.setDrawFilled(true);
                dataSet.setFillColor(Color.RED);
                dataSet.setFillAlpha(50);

                lineData.addDataSet(dataSet);




                lineChart.setData(lineData);
                lineChart.setTouchEnabled(false);
                Description d = new Description();
                d.setText("");
                lineChart.setDescription(d);
                lineChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
                lineChart.getAxisLeft().setTextColor(Color.RED);
                lineChart.getAxisLeft().setDrawGridLines(true);
                lineChart.getAxisRight().setTextColor(green);
                lineChart.getAxisLeft().setAxisMinimum(0);
                lineChart.getAxisRight().setDrawGridLines(false);
                lineChart.getAxisRight().setAxisMinimum(0);
                lineChart.getXAxis().setEnabled(false);
                lineChart.invalidate();






                int corrects = 0;
                int total = 0;
                int notTrained = 0;
                double maxDays = 0;
                int cardsIn24 = 0;
                for(Flashcard card : cards) {
                    int minLength = Math.min(5, card.history.length());
                    if(card.history.length() == 0) notTrained++;
                    for(int i = 0; i < minLength; ++i) {
                        if(card.history.charAt(i) == '1') corrects++;
                    }
                    double days = card.getDeltaTimeMillis() / 86400000.0;
                    if(days < 1.0) cardsIn24++;
                    if(days > maxDays) maxDays = days;
                    total += 5;
                }
                int percentage = Math.round(100f*corrects/total);


                String t = "Overall score: " + percentage + "%\n" +
                        "Most dustiest card: " + new DecimalFormat("##.##").format(maxDays) + " days\n" +
                        "Cards not seen yet: " + notTrained + "\n" +
                        "Trained in last 24h: " + cardsIn24;

                text.setText(t);

            }
        });



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStudyFragmentInteractionListener) {
            mListener = (OnStudyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnStudyFragmentInteractionListener {
        void onSmartStudy();
    }
}
