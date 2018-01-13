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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        db.getAllCards(new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(Flashcard[] cards) {

                double[] urgencies = new double[cards.length];
                Integer[] scores = new Integer[cards.length];
                int[] times = new int[cards.length];

                float max = Float.MIN_VALUE;
                float maxDelta = Float.MIN_VALUE;

                for(int i = 0; i < cards.length; ++i) {
                    urgencies[i] = cards[i].getUrgency();
                    if(urgencies[i] > max) max = (float)urgencies[i];
                    scores[i] = cards[i].getScore() + 5;
                    times[i] = Math.round(cards[i].getDeltaTimeMillis() / 1000f);
                    if(times[i] > maxDelta) maxDelta = times[i];
                }

                Arrays.sort(urgencies);
                Arrays.sort(scores, Collections.reverseOrder());
                Arrays.sort(times);


                LineData lineData = new LineData();
                List<Entry> entries;
                LineDataSet dataSet;


                entries = new ArrayList<>();
                for(int i = 0; i < urgencies.length; ++i) {
                    entries.add(new Entry(i, (float)urgencies[i]));
                }
                dataSet = new LineDataSet(entries, "Urgency");
                dataSet.setDrawCircles(false);
                dataSet.setColor(Color.RED);

                lineData.addDataSet(dataSet);




                entries = new ArrayList<>();
                for(int i = 0; i < scores.length; ++i) {
                    entries.add(new Entry(i, scores[i] / 11f * max));
                }
                dataSet = new LineDataSet(entries, "Score");
                dataSet.setDrawCircles(false);
                dataSet.setColor(Color.BLUE);

                lineData.addDataSet(dataSet);





                int green = Color.rgb(0, 180, 0);
                entries = new ArrayList<>();
                for(int i = 0; i < times.length; ++i) {
                    entries.add(new Entry(i, times[i] / 86400f));
                }
                dataSet = new LineDataSet(entries, "Delta Time (days)");
                dataSet.setDrawCircles(false);
                dataSet.setColor(green);
                dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

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
                lineChart.getAxisRight().setDrawGridLines(false);
                lineChart.getXAxis().setEnabled(false);
                lineChart.invalidate();


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
