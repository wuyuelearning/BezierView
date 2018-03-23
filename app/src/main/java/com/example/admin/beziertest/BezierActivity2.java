package com.example.admin.beziertest;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by admin on 2018/3/23.
 */

public class BezierActivity2 extends AppCompatActivity{
    BezierView2 bezierView2;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bezierlayout2);
        bezierView2 = (BezierView2) findViewById(R.id.bezierview2);
    }


}
