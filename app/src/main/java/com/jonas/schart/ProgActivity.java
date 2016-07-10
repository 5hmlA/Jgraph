package com.jonas.schart;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jonas.jdiagram.progress.ChargingView;
import com.jonas.jdiagram.progress.JProgBall;
import com.jonas.jdiagram.progress.JProgress;

import java.util.Random;

public class ProgActivity extends AppCompatActivity {
    private JProgBall mBall;
    private JProgress mPm;
    private ChargingView mChv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prog);
        mBall = (JProgBall) findViewById(R.id.progress);
        mChv = (ChargingView) findViewById(R.id.chv);
        mChv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChv.setCurrent(new Random().nextInt(90) + 150);

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mChv.setCurrent(new Random().nextInt(90) + 150);
            }
        }, 1000);

        mBall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mBall.mAnimator.cancel();
                mBall.setProgressMode(mBall.getProgressMode() == 0 ? 1 : 0);
                mBall.postInvalidate();
                return true;
            }
        });
        mBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBall.mAnimator.cancel();
            }
        });
        mPm = (JProgress) findViewById(R.id.progress_msg);
        mPm.setCurrentPercent(89.99f);
//        mPm.setCurrent(89.99f);
        mBall.setProgressCurrentAni(400);
//        mBall.animateShow();
    }

    public void showAni(View v) {
        mBall.animateShow();
        mPm.animateShow();
    }

    public void lianji(View v) {
        mBall.setProgressCurrent(940);
        mBall.postInvalidate();
        mPm.setCurrentAni(189.99f);
    }
}