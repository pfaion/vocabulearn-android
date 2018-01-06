package pfaion.vocabulearn;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import pfaion.vocabulearn.database.Flashcard;

public class CardViewActivity extends AppCompatActivity
implements CardFragment.OnFragmentInteractionListener{
    public static final String TAG = "Vocabulearn";

    private static enum Result {
        NOT_ANSWERED,
        WRONG,
        CORRECT,
    }


    private Flashcard[] cards;
    private boolean[] frontFirst;
    private boolean[] front;
    private boolean[] turnedBefore;
    private Result[] results;
    private int i;


    private String currentText() {
        if(front[i]) {
            return cards[i].front;
        } else {
            return cards[i].back;
        }
    }


    private ImageButton buttonWrong;
    private ImageButton buttonFlip;
    private ImageButton buttonCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        buttonWrong = findViewById(R.id.button_wrong);
        buttonFlip = findViewById(R.id.button_flip);
        buttonCorrect = findViewById(R.id.button_correct);

        buttonWrong.setVisibility(View.INVISIBLE);
        buttonCorrect.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if(intent.hasExtra("cards")) {
            cards = (Flashcard[]) intent.getSerializableExtra("cards");
            frontFirst = new boolean[cards.length];
            front = new boolean[cards.length];
            turnedBefore = new boolean[cards.length];
            results = new Result[cards.length];
            i = 0;
            for(int j = 0; j < cards.length; ++j) {
                frontFirst[j] = true;
                front[j] = frontFirst[j];
                turnedBefore[j] = false;
                results[j] = Result.NOT_ANSWERED;
            }
            showSlideLeft();
            updateUI();
        }




        final GestureDetector flipButtonGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                flipCard();
                return true;
            }
        });
        buttonFlip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                flipButtonGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        final GestureDetector wrongButtonGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Result prevResult = results[i];
                results[i] = Result.WRONG;
                updateUI();
                if(prevResult == Result.NOT_ANSWERED) nextCard();
                return true;
            }
        });
        buttonWrong.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                wrongButtonGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        final GestureDetector correctButtonGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Result prevResult = results[i];
                results[i] = Result.CORRECT;
                updateUI();
                if(prevResult == Result.NOT_ANSWERED) nextCard();
                return true;
            }
        });
        buttonCorrect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                correctButtonGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });



    }




    private void updateUI() {
        ((TextView)findViewById(R.id.cardViewTitle)).setText("Card Set " + (i+1) + "/" + cards.length);
        int progress = Math.round(100f/cards.length*(i+1));
        ((ProgressBar)findViewById(R.id.cardViewProgress)).setProgress(progress);
        switch (results[i]) {
            case NOT_ANSWERED:
                buttonWrong.setColorFilter(Color.WHITE);
                buttonCorrect.setColorFilter(Color.WHITE);
                break;
            case WRONG:
                buttonWrong.setColorFilter(Color.RED);
                buttonCorrect.setColorFilter(Color.WHITE);
                break;
            case CORRECT:
                buttonWrong.setColorFilter(Color.WHITE);
                buttonCorrect.setColorFilter(Color.GREEN);
                break;
        }
    }

    private void nextCard() {
        if(i < cards.length - 1) {
            i++;
            showSlideLeft();
            updateUI();
        }
    }

    private void prevCard() {
        if(i > 0) {
            i--;
            showSlideRight();
            updateUI();
        }
    }


    private void flipCard() {
        front[i] = !front[i];
        if(!turnedBefore[i]) {
            buttonWrong.setVisibility(View.VISIBLE);
            buttonCorrect.setVisibility(View.VISIBLE);
            turnedBefore[i] = true;
        }
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
    public void onSwipeLeft() {
        nextCard();
    }

    @Override
    public void onSwipeRight() {
        prevCard();
    }
}
