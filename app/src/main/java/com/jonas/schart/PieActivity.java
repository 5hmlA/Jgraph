package com.jonas.schart;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jonas.jgraph.graph.pie.JPieView;
import com.jonas.jgraph.models.Apiece;

import java.util.ArrayList;
import java.util.List;

public class PieActivity extends AppCompatActivity implements View.OnClickListener {

    private JPieView pie;
    private Button mCenter;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        //		requestWindowFeature(Window.FEATURE_NO_TITLE);
        //        Window window = getWindow();
        //        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie);
        pie = (JPieView) findViewById(R.id.rp);
        mCenter = (Button) findViewById(R.id.center);
        mCenter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pie.setTstartAtTouch(!pie.isTstartAtTouch());
                Toast.makeText(getApplicationContext(), pie.isTstartAtTouch() ? "startAtTouch" : "startAtNei", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        pie.setOnClickListener(this);
        pie.setPieShowAnimation(JPieView.PieShowAnimation.GROWING);
        //        pie.setShowCenterAll(true);
        pie.setShowInCenAngle(true);
        pie.setOutMoving(true);
        setdata();
        pie.setShowCenterAll(false);
        pie.setTstartAtTouch(true);
    }

    public void refresh(View view) {
        pie.setEachPieColor();
        pie.invalidate();
    }

    public void showAni(View view) {
        i++;
        switch (i % 3) {
            case 0:
                pie.setPieShowAnimation(JPieView.PieShowAnimation.GROWING);
                break;
            case 1:
                pie.setPieShowAnimation(JPieView.PieShowAnimation.FILLOUTING);
                pie.aniShowPie();
                ObjectAnimator oa = ObjectAnimator.ofFloat(pie, "showProgress", 0, 1);
                oa.setDuration(2000);
                oa.start();
                break;
            case 2:
                pie.setPieShowAnimation(JPieView.PieShowAnimation.SCANNING);
        }
        pie.aniShowPie();
    }

    public void showLine(View v) {
        pie.setShowCenterAll(!pie.isShowCenterAll());
    }

    @Override
    public void onClick(View v) {
        pie.invalidate();
    }

    public void clickSelector(View v) {
        pie.setMovingShowTX(!pie.isMovingShowTX());
    }

    int i = 30;

    public void rotateAble(View view) {
        pie.setDegrees(i = i + 40);
        pie.invalidate();
    }

    long[] mHits = new long[5];

    public void lianji(View v) {
        /*
         * src the source array to copy the content.    copy的原数组
		srcPos the starting index of the content in src.   在数据哪个位置开复制
		dst the destination array to copy the data into.   复制到哪个目标数组
		dstPos the starting index for the copied content in dst. 在目标数组的那个位置开始复制
		length the number of elements to be copied.   // 复制数组的长度
		 *
		 *
		 */
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);  //  数组的值 向左移动1位
        mHits[mHits.length - 1] = SystemClock.uptimeMillis(); //SystemClock.uptimeMillis();  距离开机的时间
        if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
            // 三击事件
            Toast.makeText(getApplicationContext(), "5击事件", Toast.LENGTH_SHORT).show();
            List<Apiece> piedata = new ArrayList<Apiece>();
            Apiece pie1 = new Apiece(Color.parseColor("#9FD255"), 60f);
            Apiece pie2 = new Apiece(Color.parseColor("#C6E893"), 98f);
            Apiece pie3 = new Apiece(Color.parseColor("#EDF5E2"), 88f);
            Apiece pie4 = new Apiece(Color.parseColor("#EDF5E2"), 32f);
            piedata.add(pie1);
            piedata.add(pie2);
            piedata.add(pie3);
            piedata.add(pie4);
            pie.feedData(piedata);
        }
    }

    private void setdata() {
        List<Float> data = new ArrayList<Float>();
        data.add(45f);
        data.add(35f);
        data.add(42f);
        data.add(15f);
        data.add(35f);
        pie.feedData2(data);
        List<String> desc = new ArrayList<>();
        desc.add("33");
        desc.add("23");
        desc.add("13");
        desc.add("13");
        desc.add("13");
        pie.setDescPiedata(desc);
    }

    public void center(View v) {
        pie.setShowInCenAngle(!pie.isShowInCenAngle());
        mCenter.setText(pie.isShowInCenAngle() ? "center" : "atTouch");
    }
}
