package com.smartitventures.quickdelivery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kwiqdelivery.R;

import butterknife.ButterKnife;

public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);

    }
}
