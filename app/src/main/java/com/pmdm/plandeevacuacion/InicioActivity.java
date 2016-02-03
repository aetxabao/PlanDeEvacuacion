package com.pmdm.plandeevacuacion;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Typeface tf = Typeface.createFromAsset(this.getAssets(),
                "fonts/plstk.ttf");
        TextView textView = (TextView) findViewById(R.id.AppTitle);
        textView.setTypeface(tf);
    }

    public void jugar(View v){
        Intent intent = new Intent(InicioActivity.this,JuegoActivity.class);
        startActivity(intent);
    }
}
