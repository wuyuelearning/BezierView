package com.example.admin.beziertest;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by admin on 2018/3/23.
 */

public class BezierActivity1  extends AppCompatActivity{

    BezierView bezierView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bezierlayout);
        bezierView = (BezierView)findViewById(R.id.bezierview);
    }
}
