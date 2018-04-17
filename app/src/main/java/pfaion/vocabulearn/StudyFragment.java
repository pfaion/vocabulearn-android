package pfaion.vocabulearn;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Debug;
import android.util.Log;
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
        final TextView label0 = (TextView)view.findViewById(R.id.label0);
        final TextView label11 = (TextView)view.findViewById(R.id.label11);
        final TextView label12 = (TextView)view.findViewById(R.id.label12);
        final TextView label21 = (TextView)view.findViewById(R.id.label21);
        final TextView label22 = (TextView)view.findViewById(R.id.label22);
        final TextView label31 = (TextView)view.findViewById(R.id.label31);
        final TextView label32 = (TextView)view.findViewById(R.id.label32);

        db.getAllCards(new Data.LoadedCb<Flashcard[]>() {
            @Override
            public void onSuccess(Flashcard[] cards) {
                if(getActivity() == null) return;

                if(cards.length == 0) return;


                int n_sides = 0;
                for(Flashcard card : cards) {
                    n_sides += (card.front_first) ? 1 : 2;
                }


                final double[] urgencies = new double[n_sides];
                final boolean[] frontFirst = new boolean[n_sides];
                final int[] times = new int[n_sides];
                Integer[] indices = new Integer[n_sides];

                float max = Float.MIN_VALUE;
                float maxDelta = Float.MIN_VALUE;


                int s = 0;
                for(int c = 0; c < cards.length; ++c) {
                    frontFirst[s] = true;
                    indices[s] = s;
                    urgencies[s] = cards[c].getUrgency();
                    if (urgencies[s] > max) {
                        max = (float) urgencies[s];
                        Log.d("MAX URG", "" + max + " id: " + cards[c].id);
                    }
                    times[s] = Math.round(cards[c].getDeltaTimeMillis() / 1000f);
                    if (times[s] > maxDelta) maxDelta = times[s];
                    s++;
                    if(!cards[c].front_first) {
                        frontFirst[s] = false;
                        indices[s] = s;
                        urgencies[s] = cards[c].getUrgencyBack();
                        if (urgencies[s] > max) {
                            max = (float) urgencies[s];
                            Log.d("MAX URG", "" + max + " id: " + cards[c].id);
                        }
                        times[s] = Math.round(cards[c].getDeltaTimeMillisBack() / 1000f);
                        if (times[s] > maxDelta) maxDelta = times[s];
                        s++;
                    }
                }

                Arrays.sort(indices, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer i1, Integer i2) {
                        return Double.compare(times[i1], times[i2]);
                    }
                });

                double[] tmpUrgencies = new double[urgencies.length];
                boolean[] tmpFrontFirst = new boolean[frontFirst.length];
                int[] tmpTimes = new int[times.length];
                for(int i = 0; i < indices.length; ++i) {
                    tmpUrgencies[i] = urgencies[indices[i]];
                    tmpFrontFirst[i] = frontFirst[indices[i]];
                    tmpTimes[i] = times[indices[i]];
                }

                for(int i = 0; i < indices.length; ++i) {
                    urgencies[i] = tmpUrgencies[i];
                    frontFirst[i] = tmpFrontFirst[i];
                    times[i] = tmpTimes[i];
                }




                LineData lineData = new LineData();
                List<Entry> entries;
                LineDataSet dataSet;




                entries = new ArrayList<>();
                for(int i = 0; i < times.length; ++i) {
                    entries.add(new Entry(i, times[i] / 86400f));
                }
                dataSet = new LineDataSet(entries, "Days");
                int blue = Color.parseColor("#1f77b4");
                dataSet.setDrawCircles(false);
                dataSet.setColor(blue);
                dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

                lineData.addDataSet(dataSet);




                entries = new ArrayList<>();
                for(int i = 0; i < urgencies.length; ++i) {
                    entries.add(new Entry(i, (float)urgencies[i]));
                }
                dataSet = new LineDataSet(entries, "Urgency");
                int orange = Color.parseColor("#ff7f0e");
                dataSet.setDrawCircles(false);
                dataSet.setColor(orange);
                dataSet.setDrawFilled(true);
                dataSet.setFillColor(orange);
                dataSet.setFillAlpha(50);

                lineData.addDataSet(dataSet);



                if(max > 10) {
                    entries = new ArrayList<>();
                    entries.add(new Entry(0, 10));
                    entries.add(new Entry(urgencies.length - 1, 10));
                    dataSet = new LineDataSet(entries, "limit");
                    int red = Color.parseColor("#d62728");
                    dataSet.setDrawCircles(false);
                    dataSet.setColor(red);
                    dataSet.enableDashedLine(10, 10, 0);

                    lineData.addDataSet(dataSet);
                }




                lineChart.setData(lineData);
                lineChart.setTouchEnabled(false);
                Description d = new Description();
                d.setText("");
                lineChart.setDescription(d);
                lineChart.setBackgroundColor(getResources().getColor(R.color.primaryDarkColor, null));
                lineChart.getLegend().setEnabled(false);
                lineChart.getAxisLeft().setEnabled(false);
                lineChart.getAxisLeft().setAxisMinimum(0);
                lineChart.getAxisLeft().setAxisMaximum(Math.max(max, 10f));
                lineChart.getAxisRight().setEnabled(false);
                lineChart.getAxisRight().setAxisMinimum(0);
                lineChart.getXAxis().setEnabled(false);
                lineChart.invalidate();






                int corrects = 0;
                int total = 0;
                int notTrained = 0;
                double maxDays = 0;
                int cardsIn24 = 0;
                int marked = 0;
                for(int i = 0; i < cards.length; ++i) {
                    Flashcard card = cards[i];
                    String history = card.history;
                    int minLength = Math.min(5, history.length());
                    if(history.length() == 0) notTrained++;
                    for(int j = 0; j < minLength; ++j) {
                        if(history.charAt(j) == '1') corrects += (5 - j);
                    }
                    double days = card.getDeltaTimeMillis() / 86400000.0;
                    if(days < 1.0) cardsIn24++;
                    if(days > maxDays) maxDays = days;
                    total += 15;
                    if(card.marked) marked++;
                    if(!card.front_first) {
                        history = card.history_back;
                        minLength = Math.min(5, history.length());
                        if(history.length() == 0) notTrained++;
                        for(int j = 0; j < minLength; ++j) {
                            if(history.charAt(j) == '1') corrects += (5 - j);
                        }
                        days = card.getDeltaTimeMillisBack() / 86400000.0;
                        if(days < 1.0) cardsIn24++;
                        if(days > maxDays) maxDays = days;
                        total += 15;
                    }
                }
                int percentage = Math.round(100f*corrects/total);


//                String t = "Overall score: " + percentage + "%\n" +
//                        "Max urgency: " + new DecimalFormat("##.##").format(max) + "\n" +
//                        "Most dustiest card: " + new DecimalFormat("##.##").format(maxDays) + " days\n" +
//                        "Cards : " + cards.length + "\n" +
//                        "Cards not seen yet: " + notTrained + "\n" +
//                        "Cards marked: " + marked + "\n" +
//                        "Trained in last 24h: " + cardsIn24;
//                text.setText(t);

                label0.setText("" + percentage + "%");
                label11.setText("" + cards.length);
                label12.setText(new DecimalFormat("##.#").format(max));
                label21.setText("" + notTrained);
                label22.setText("" + cardsIn24);
                label31.setText("" + marked);
                label32.setText("" + (int)maxDays + "d " + Math.round((maxDays - Math.floor(maxDays))*24) + "h");


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
