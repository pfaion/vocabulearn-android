package pfaion.vocabulearn;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;

import pfaion.vocabulearn.database.Flashcard;

public class CardViewActivity extends AppCompatActivity
implements CardFragment.OnFragmentInteractionListener{
    public static final String TAG = "Vocabulearn";
    private Swipe swipe;

    private Flashcard[] cards;
    private int i;
    private boolean front;

    private boolean frontDefault = true;

    private String currentText() {
        if(front) {
            return cards[i].front;
        } else {
            return cards[i].back;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        Intent intent = getIntent();
        if(intent.hasExtra("cards")) {
            cards = (Flashcard[]) intent.getSerializableExtra("cards");
            i = 0;
            front = true;
            showSlideLeft();
            updateProgress();
        }

//        findViewById(R.id.button_prev).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                prevCard();
//            }
//        });

        findViewById(R.id.button_flip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard();
            }
        });

//        findViewById(R.id.button_next).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nextCard();
//            }
//        });

        swipe = new Swipe();
        swipe.setListener(new SwipeListener() {
            @Override public void onSwipingLeft(final MotionEvent event) {
            }

            @Override public void onSwipedLeft(final MotionEvent event) {
                nextCard();
            }

            @Override public void onSwipingRight(final MotionEvent event) {
            }

            @Override public void onSwipedRight(final MotionEvent event) {
                prevCard();
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



    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        swipe.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void updateProgress() {
        ((TextView)findViewById(R.id.cardViewTitle)).setText("Card Set " + (i+1) + "/" + cards.length);
        int progress = Math.round(100f/cards.length*(i+1));
        ((ProgressBar)findViewById(R.id.cardViewProgress)).setProgress(progress);
    }

    private void nextCard() {
        if(i < cards.length - 1) {
            i++;
            front = frontDefault;
            showSlideLeft();
            updateProgress();
        }
    }

    private void prevCard() {
        if(i > 0) {
            i--;
            front = !frontDefault;
            showSlideRight();
            updateProgress();
        }
    }


    private void flipCard() {
        front = !front;
        showFlip();
    }


    private void showFlip() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.animator.card_flip_right_in,
                R.animator.card_flip_right_out,
                R.animator.card_flip_left_in,
                R.animator.card_flip_left_out);
        transaction.replace(R.id.frameLayout, CardFragment.newInstance(currentText()));
        transaction.commit();
    }

    private void showSlideLeft() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
        transaction.replace(R.id.frameLayout, CardFragment.newInstance(currentText()));
        transaction.commit();
    }

    private void showSlideRight() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);
        transaction.replace(R.id.frameLayout, CardFragment.newInstance(currentText()));
        transaction.commit();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }



    @Override
    public void onFragmentInteraction() {

    }
}
