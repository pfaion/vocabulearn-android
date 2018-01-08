package pfaion.vocabulearn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;

import java.util.ArrayList;
import java.util.List;

import pfaion.vocabulearn.CardViewActivity.ResultType;

public class ResultsFragment extends Fragment {


    private CardFragment.OnFragmentInteractionListener mListener;

    private Swipe swipe;

    private View.OnClickListener cb;
    public ResultsFragment() {}

    public static ResultsFragment newInstance(ResultType[] results, View.OnClickListener cb) {
        ResultsFragment fragment = new ResultsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_results, container, false);


        swipe = new Swipe();
        swipe.setListener(new SwipeListener() {
            @Override public void onSwipingLeft(final MotionEvent event) {
            }

            @Override public void onSwipedLeft(final MotionEvent event) {
                mListener.onSwipeLeft();
            }

            @Override public void onSwipingRight(final MotionEvent event) {
            }

            @Override public void onSwipedRight(final MotionEvent event) {
                mListener.onSwipeRight();
            }

            @Override public void onSwipingUp(final MotionEvent event) {
            }

            @Override public void onSwipedUp(final MotionEvent event) {
            }

            @Override public void onSwipingDown(final MotionEvent event) {
            }

            @Override public void onSwipedDown(final MotionEvent event) {
            }
        });



        CustomResultsCardLayout cardLayout = view.findViewById(R.id.card_layout);
        cardLayout.setCb(new CustomResultsCardLayout.Callback() {
            @Override
            public void dispatchTouchEvent(MotionEvent ev) {
                swipe.dispatchTouchEvent(ev);
            }
        });


        PieChart pieChart = view.findViewById(R.id.chart);


        ResultType[] results = (ResultType[]) getArguments().getSerializable("results");
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
        Description d = new Description();
        d.setText("");
        pieChart.setDescription(d);
        pieChart.setCenterTextTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setTransparentCircleColor(getResources().getColor(R.color.primaryDarkColor, null));
        pieChart.setHoleColor(getResources().getColor(R.color.primaryDarkColor, null));
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();





        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CardFragment.OnFragmentInteractionListener) {
            mListener = (CardFragment.OnFragmentInteractionListener) context;
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


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.results_dialog, null);

        PieChart pieChart = view.findViewById(R.id.chart);


        ResultType[] results = (ResultType[]) getArguments().getSerializable("results");
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
//                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}