package com.white.progress;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.white.progressview.KevenProgressView;

/**
 * Created by keven on 2018/11/29.
 */

public class SuperSonicAcitivity extends Activity {

    private KevenProgressView ss_a4_0;
    private KevenProgressView ss_a4_1;
    private KevenProgressView ss_a4_2;
    private KevenProgressView ss_a4_3;
    private KevenProgressView ss_a4_4;
    private KevenProgressView ss_a4_5;
    private KevenProgressView ss_a4_6;
    private KevenProgressView ss_a4_7;

    private ImageButton button_backward;

    private AlertDialog tipsDialog;


//    3:A3    4:A4
    private int robot_vesion = 4;

    private long lastTimemm=0 ;

    private boolean isInitSucced = false;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_supersonic);

        inistView();




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void inistView(){



        if (robot_vesion==3){
//            setContentView(R.layout.activity_supersonic_a3);
            ss_a4_0 =(KevenProgressView)findViewById(R.id.ss_a4_4);
            ss_a4_2 =(KevenProgressView)findViewById(R.id.ss_a4_2);

            ss_a4_1 =(KevenProgressView)findViewById(R.id.ss_a4_7);
            ss_a4_3 =(KevenProgressView)findViewById(R.id.ss_a4_6);
            ss_a4_4 =(KevenProgressView)findViewById(R.id.ss_a4_5);
            ss_a4_5 =(KevenProgressView)findViewById(R.id.ss_a4_3);
            ss_a4_6 =(KevenProgressView)findViewById(R.id.ss_a4_1);
            ss_a4_7 =(KevenProgressView)findViewById(R.id.ss_a4_0);


            ss_a4_0.setVisibility(View.INVISIBLE);
            ss_a4_2.setVisibility(View.INVISIBLE);


//            ImageView dipan = (ImageView)findViewById(R.id.dipan);
//            dipan.setImageResource(R.drawable.robot_a3_underpan1);

        }else if (robot_vesion==4){
            ss_a4_0 =(KevenProgressView)findViewById(R.id.ss_a4_0);
            ss_a4_1 =(KevenProgressView)findViewById(R.id.ss_a4_1);
            ss_a4_2 =(KevenProgressView)findViewById(R.id.ss_a4_2);
            ss_a4_3 =(KevenProgressView)findViewById(R.id.ss_a4_3);
            ss_a4_4 =(KevenProgressView)findViewById(R.id.ss_a4_4);
            ss_a4_5 =(KevenProgressView)findViewById(R.id.ss_a4_5);
            ss_a4_6 =(KevenProgressView)findViewById(R.id.ss_a4_6);
            ss_a4_7 =(KevenProgressView)findViewById(R.id.ss_a4_7);
        }

    }


    public void startWithAnim(View view) {
        ss_a4_0.runProgressAnim(1000);
        ss_a4_1.runProgressAnim(2000);
        ss_a4_2.runProgressAnim(3000);
        ss_a4_3.runProgressAnim(4000);
        ss_a4_4.runProgressAnim(5000);

        ss_a4_5.runProgressAnim(1000);
        ss_a4_6.runProgressAnim(2000);
        ss_a4_7.runProgressAnim(3000);
    }


}
