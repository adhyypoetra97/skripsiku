package com.spk.dispepsia.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.spk.dispepsia.R;

public class Tentang extends AppCompatActivity {

    private int countFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
    }

    public int getCountFragment() {
        return countFragment;
    }

    @Override
    public void onBackPressed() {
        int count = getCountFragment();

        if(count == 0) {
            getFragmentManager().popBackStack();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}