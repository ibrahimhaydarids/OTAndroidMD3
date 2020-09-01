package com.ids.fixot.classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Amal on 7/19/2017.
 */

public class TickerRecyclerView extends RecyclerView {

    private boolean lock = false;

    public TickerRecyclerView(Context context) {
        super(context);

    }

    public TickerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TickerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!lock) {

            return super.onTouchEvent(ev);
        } else {
            return true;
        }

    }


    /**
     * @return the lock
     */
    public boolean isLock() {
        return lock;

    }

    /**
     * @param lock the lock to set
     */
    public void setLock(boolean lock) {
        this.lock = lock;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return false;
    }
}

