package pfaion.vocabulearn;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by pfaion on 08.01.18.
 */

public class CustomResultsCardLayout extends ConstraintLayout {

    public CustomResultsCardLayout(Context context) {
        super(context);
    }
    public CustomResultsCardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomResultsCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface Callback {
        void dispatchTouchEvent(MotionEvent ev);
    }

    private Callback cb;
    public void setCb(Callback cb) {
        this.cb = cb;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        cb.dispatchTouchEvent(ev);
        return true;
    }
}
