package com.example.admin.beziertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private BezierView bezierView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bezierlayout);
        initView();
    }

    private void initView(){
        bezierView = (BezierView)findViewById(R.id.bezierview);
//        int width = bezierView.getMeasuredWidth();
//        Log.d("Tag",""+width);
    }
}
