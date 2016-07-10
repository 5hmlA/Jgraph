package com.jonas.jdiagram.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * 仿华为ishouji充电
 */
public class ChargingView extends RelativeLayout {

    private ArrayList<ChargeView> cvs = new ArrayList<>();
    private int balls = 4;
    private Context context;
    private long delay = 80;

    public ChargingView(Context context){
        super(context);
        init(context);
    }

    public ChargingView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public ChargingView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        for(int i = 0; i<balls; i++) {
            ChargeView chargeView = new ChargeView(context);
            if(i != 0) {
                chargeView.showRing = false;
            }
            cvs.add(chargeView);
        }
        for(ChargeView cv : cvs) {
            addView(cv);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

    }

    public void setCurrent(float current){
//        for(ChargeView cv : cvs) {
//            cv.setAniSweepAngle(current);
//
//        }
        for(int i = 0; i<cvs.size(); i++) {
            cvs.get(i).setAniSweepAngle(current, delay*i);
        }
    };
}
