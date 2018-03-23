package com.example.admin.beziertest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView1;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        initView();
    }

    private void initView() {
        textView1 = (TextView) findViewById(R.id.text);
        textView2 = (TextView) findViewById(R.id.text2);

        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        Intent intent = null ;
        if(v.getId() == R.id.text){
            intent = new Intent(this,BezierActivity1.class);
        } else if(v.getId() == R.id.text2){
            intent = new Intent(this,BezierActivity2.class);
        }
        startActivity(intent);
    }
}
