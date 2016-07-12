package com.jonas.schart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jonas.jgraph.progress.AniBallProgress;
import com.jonas.jgraph.progress.JWaveBallProg;
import com.jonas.jgraph.progress.JProgress;

import java.util.Random;

public class ProgActivity extends AppCompatActivity {
    private JWaveBallProg mBall;
    private JProgress mPm;
    private AniBallProgress mBallProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prog);
        mBall = (JWaveBallProg) findViewById(R.id.progress);
        mBallProg = (AniBallProgress) findViewById(R.id.chv);
        mBallProg.setBallColor(Color.RED,Color.YELLOW,Color.GREEN,Color.GRAY);
        mBallProg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBallProg.setAniProgress(new Random().nextInt(60) + 10);

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBall.setJProgress(100);
                mBallProg.setJProgress(60);
                mBallProg.postInvalidate();
            }
        }, 1000);

        mBall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mBall.mAnimator.cancel();
                mBall.setProgMode(mBall.getProgMode() == 0 ? 1 : 0);
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
//        mBall.setProgressCurrentAni(400);
//        mBall.animateShow();
    }

    public void showAni(View v) {
        mBall.setAniProgress(new Random().nextInt(250) + 10);
        mPm.animateShow();
    }

    public void lianji(View v) {
//        mBall.setProgressCurrent(940);
        mBall.postInvalidate();
        mPm.setCurrentAni(189.99f);
    }
}