package pfaion.vocabulearn;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import pfaion.vocabulearn.database.Data;
import pfaion.vocabulearn.database.Flashcard;

public class CardViewActivity extends AppCompatActivity
implements CardFragment.OnFragmentInteractionListener{
    public static final String TAG = "Vocabulearn";

    private Data db;

    public static enum ResultType {
        NOT_ANSWERED,
        WRONG,
        CORRECT,
    }


    private Flashcard[] cards;
    private boolean[] frontFirst;
    private boolean[] front;
    private boolean[] turnedBefore;
    private ResultType[] results;
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
    private LinearLayout buttonRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        db = Data.getInstance(this);

        buttonWrong = findViewById(R.id.button_wrong);
        buttonFlip = findViewById(R.id.button_flip);
        buttonCorrect = findViewById(R.id.button_correct);
        buttonRow = findViewById(R.id.linearLayout);


        Intent intent = getIntent();
        Settings settings = (Settings) intent.getSerializableExtra("settings");
        cards = (Flashcard[]) intent.getSerializableExtra("cards");

        switch (settings.order) {
            case Settings.ORDER_RANDOM:
                Random rnd = ThreadLocalRandom.current();
                for (int i = cards.length - 1; i > 0; i--)
                {
                    int index = rnd.nextInt(i + 1);
                    Flashcard a = cards[index];
                    cards[index] = cards[i];
                    cards[i] = a;
                }
                break;
            case Settings.ORDER_HARD:
                Arrays.sort(cards, new Comparator<Flashcard>() {
                    @Override
                    public int compare(Flashcard c1, Flashcard c2) {
                        float performance1 = 0;
                        float performance2 = 0;

                        int minLength1 = Math.min(5, c1.history.length());
                        for(int i = 0; i < minLength1; ++i) {
                            if(c1.history.charAt(i) == '1') performance1++;
                        }
                        performance1 /= Math.max(1, minLength1);

                        int minLength2 = Math.min(5, c2.history.length());
                        for(int i = 0; i < minLength2; ++i) {
                            if(c2.history.charAt(i) == '1') performance2++;
                        }
                        performance2 /= Math.max(1, minLength2);


                        return Float.compare(-performance2, -performance1);
                    }
                });
                break;
            case Settings.ORDER_NEW:
                Arrays.sort(cards, new Comparator<Flashcard>() {
                    @Override
                    public int compare(Flashcard c1, Flashcard c2) {
                        return Integer.compare(c1.history.length(), c2.history.length());
                    }
                });
                break;
            case Settings.ORDER_OLD:
                Arrays.sort(cards, new Comparator<Flashcard>() {
                    @Override
                    public int compare(Flashcard c1, Flashcard c2) {
                        return c1.last_trained_date.compareTo(c2.last_trained_date);
                    }
                });
                break;
            default:
                break;
        }

        Log.d(TAG, "onCreate: " + (settings.amount == Settings.AMOUNT_5));

        switch (settings.amount) {
            case Settings.AMOUNT_5:
                cards = Arrays.copyOfRange(cards, 0, Math.min(5, cards.length));
                break;
            case Settings.AMOUNT_10:
                cards = Arrays.copyOfRange(cards, 0, Math.min(10, cards.length));
                break;
            case Settings.AMOUNT_20:
                cards = Arrays.copyOfRange(cards, 0, Math.min(20, cards.length));
                break;
            case Settings.AMOUNT_30:
                cards = Arrays.copyOfRange(cards, 0, Math.min(30, cards.length));
                break;
            case Settings.AMOUNT_ALL:
                cards = Arrays.copyOfRange(cards, 0, cards.length);
                break;
            default:
                break;
        }


        frontFirst = new boolean[cards.length];
        front = new boolean[cards.length];
        turnedBefore = new boolean[cards.length];
        results = new ResultType[cards.length];
        i = 0;
        for(int j = 0; j < cards.length; ++j) {
            Random rnd = new Random();
            if(settings.side == Settings.SIDE_FRONT_FIRST) frontFirst[j] = true;
            else if(settings.side == Settings.SIDE_BACK_FIRST) frontFirst[j] = false;
            else if(settings.side == Settings.SIDE_MIXED) frontFirst[j] = rnd.nextBoolean();


            front[j] = frontFirst[j];
            turnedBefore[j] = false;
            results[j] = ResultType.NOT_ANSWERED;
        }
        showSlideLeft();
        updateUI();




        final GestureDetector flipButtonGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(i == cards.length) {
                    commitResults();
                } else {
                    flipCard();
                }
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
                ResultType prevResult = results[i];
                results[i] = ResultType.WRONG;
                updateUI();
                if(prevResult == ResultType.NOT_ANSWERED) nextCard();
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
                ResultType prevResult = results[i];
                results[i] = ResultType.CORRECT;
                updateUI();
                if(prevResult == ResultType.NOT_ANSWERED) nextCard();
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
        if(i == cards.length) {
            ((TextView) findViewById(R.id.cardViewTitle)).setText("Results");
            ((ProgressBar) findViewById(R.id.cardViewProgress)).setProgress(100);
            buttonWrong.setVisibility(View.INVISIBLE);
            buttonCorrect.setVisibility(View.INVISIBLE);
        } else {
            ((TextView) findViewById(R.id.cardViewTitle)).setText("Card Set " + (i + 1) + "/" + cards.length);
            int progress = Math.round(100f / cards.length * (i + 1));
            ((ProgressBar) findViewById(R.id.cardViewProgress)).setProgress(progress);
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
            if (turnedBefore[i] || frontFirst[i] != front[i]) {
                buttonWrong.setVisibility(View.VISIBLE);
                buttonCorrect.setVisibility(View.VISIBLE);
            } else {
                buttonWrong.setVisibility(View.INVISIBLE);
                buttonCorrect.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void nextCard() {
        if(i < cards.length - 1) {
            i++;
            showSlideLeft();
            updateUI();
        } else if(i == cards.length - 1) {
            i++;
            showResultsSlideLeft();
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


    @Override
    public void onBackPressed() {
        i = cards.length;
        showResultsSlideLeft();
        updateUI();
    }


    private void commitResults() {
        for(int i = 0; i < cards.length; ++i) {
            String history = cards[i].history;
            switch (results[i]) {
                case CORRECT:
                    history = "1" + history;
                    break;
                case WRONG:
                    history = "0" + history;
                    break;
                default: break;
            }
            if(history.length() > 16) {
                history = history.substring(0, 16);
            }
            cards[i].history = history;
        }
        db.updateCards(cards, results, new Data.LoadedCb<String>() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }





    private void flipCard() {
        front[i] = !front[i];
        if(!turnedBefore[i]) {
            turnedBefore[i] = true;
        }
        updateUI();
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

    private void showResultsSlideLeft() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
        transaction.replace(R.id.frameLayout, ResultsFragment.newInstance(results, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: RESULTS BUTTON!");
            }
        }));
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
