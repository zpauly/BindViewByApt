package com.zpauly.bindviewbyapt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zpauly.annotations.BindView;
import com.zpauly.bind.Bind;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.test_text)
    public TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bind.bindView(this);

        mTextView.setText("bind success");
    }
}
