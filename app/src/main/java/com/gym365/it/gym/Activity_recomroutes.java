package com.gym365.it.gym;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
public class Activity_recomroutes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomroutes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void clickRuta1(View view) {

        Intent intent = new Intent (this, Activity_recomroutesmap.class);
        Button button1 = (Button) findViewById(R.id.ruta1);

        Bundle b1 = new Bundle();
        b1.putInt("RUTA", 1);
        intent.putExtras(b1);

        startActivity(intent);

    }

    public void clickRuta2(View view) {

        Intent intent = new Intent (this, Activity_recomroutesmap.class);
        Button button2 = (Button) findViewById(R.id.ruta2);

        Bundle b2 = new Bundle();
        b2.putInt("RUTA", 2);
        intent.putExtras(b2);

        startActivity(intent);

    }

    public void clickRuta3(View view) {

        Intent intent = new Intent (this, Activity_recomroutesmap.class);
        Button button3 = (Button) findViewById(R.id.ruta3);

        Bundle b3 = new Bundle();
        b3.putInt("RUTA", 3);
        intent.putExtras(b3);

        startActivity(intent);

    }

}
