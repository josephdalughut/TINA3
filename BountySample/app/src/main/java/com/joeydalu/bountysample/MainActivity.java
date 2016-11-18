package com.joeydalu.bountysample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    MongolViewGroup viewGroup;
    EditText editText;
    int newWidth = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewGroup = (MongolViewGroup) findViewById(R.id.viewGroup);
        editText = (EditText) findViewById(R.id.editText);
    }

    public void buttonClicked(View view) {

        newWidth += 200;
        ViewGroup.LayoutParams params = viewGroup.getLayoutParams();
        params.width=newWidth;
        viewGroup.setLayoutParams(params);
    }

}
