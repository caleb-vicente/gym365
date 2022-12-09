package com.gym365.it.gym;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;


import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_add_exercises extends AppCompatActivity {
    private DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

/*Cada una de las siguientes funciones llama a la actividad que muestra el listado de ejercicios por categoria
    pero añadiendo un bundle, indicando la categoría concreta*/
    public void clickPecho(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "pecho";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }
    public void clickPiernas(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "pierna";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }
    public void clickEspalda(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "espalda";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }

    public void clickCardio(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "cardio";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }
    public void clickBiceps(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "biceps";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }
    public void clickAbdominal(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "abdominal";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }
    public void clickTriceps(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "triceps";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }
    public void clickHombro(View view) {

        Intent intent = new Intent (Activity_add_exercises.this, Activity_add_category_exercise.class);
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table  = select_name_table.getString("NAME_TABLE");
        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        String category_string = "hombro";
        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putLong("ID",category_id);
        category.putString("ACTIVITY", activity);
        category.putString("NAME_TABLE", name_table);
        intent.putExtras(category);

        startActivity(intent);

    }

    public void save_dates(View view){

        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");


        if (activity.equals("tables")){
            Intent intent = new Intent (Activity_add_exercises.this, Activity_tables.class);
            startActivity(intent);
        } else if (activity.equals( "add_table")){
            Intent intent = new Intent (Activity_add_exercises.this, Activity_add_table.class);

            Bundle date_cal = getIntent().getExtras();
            String date = date_cal.getString("DATE");

            Bundle category = new Bundle();
            category.putString("DATE", date);
            intent.putExtras(category);

            startActivity(intent);
        }
    }

    public void onBackPressed(){

        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        //borramos la tabla
        delete_table(category_id);

    }

    public void delete_table(final long id_table) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.dialog_title_remove_table);
        ad.setMessage(R.string.message_dialog_remove_table_without_save);
        ad.setIcon(R.drawable.icono_basura);
        ad.setPositiveButton(R.string.dialog_buttonok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dbAdapter.delete_all_exercises_from_table(id_table);
                        dbAdapter.delete_table(id_table);
                        Intent intent = new Intent (Activity_add_exercises.this, Activity_tables.class);
                        startActivity(intent);
                    }
                });
        ad.setNegativeButton(R.string.dialog_buttonCancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });

        ad.show();

    }

}