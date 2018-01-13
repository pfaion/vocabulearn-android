package pfaion.vocabulearn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;


public class CardFragment extends Fragment {
    public static final String TAG = "Vocabulearn";

    private String text;
    private boolean isLeech;

    private OnFragmentInteractionListener mListener;

    private Swipe swipe;

    public CardFragment() {
    }

    public static CardFragment newInstance(String text, boolean isLeech) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        args.putBoolean("isLeech", isLeech);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString("text");
            isLeech = getArguments().getBoolean("isLeech");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);

        TextView textView = view.findViewById(R.id.textView5);
        if(isLeech) {
            textView.setTextColor(Color.rgb(200, 0, 0));
        }
        textView.setText(text);


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





        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipe.dispatchTouchEvent(motionEvent);
                return true;
            }
        });


        return view;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSwipeLeft();
        void onSwipeRight();
    }
}
