package com.example.amence_a.drawlayoutview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SlideMenu sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sm = (SlideMenu) findViewById(R.id.sm);
        findViewById(R.id.ib_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        sm.switchState();
    }

    public void onTabClick(View view) {
        Toast.makeText(this, "我是菜单", Toast.LENGTH_SHORT).show();
    }

}
