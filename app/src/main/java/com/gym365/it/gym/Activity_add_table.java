package com.gym365.it.gym;

/* Para el selector de fecha y hora:
    https://www.youtube.com/watch?v=y3exATaC0kA
 */

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

//import android.support.v7.app.AppCompatActivity;

public class Activity_add_table extends AppCompatActivity implements View.OnClickListener {

    private Long mRowId;
    private DbAdapter dbAdapter;

    private EditText nombreText, subtituloText, diaText, horaInicioText;
    Button confirmButton, bfecha, bhoraInicio;
    private int dia, mes, ano, horaInicio, minutosInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // infla el layout
        setContentView(R.layout.activity_add_table);

        // obtiene referencia a los 4 views que componen el layout de esta clase y los botones
        nombreText = (EditText) findViewById(R.id.name_table_add_table);
        diaText = (EditText) findViewById(R.id.dia_tabla);
        horaInicioText = (EditText) findViewById(R.id.hora_inicio_tabla);
        subtituloText = (EditText) findViewById(R.id.subtitulo_tabla);

        confirmButton = (Button) findViewById(R.id.confirm);
        bfecha = (Button) findViewById(R.id.bfecha);
        bhoraInicio = (Button) findViewById(R.id.bhoraInicio);

        // escuchadores de los botones
        bfecha.setOnClickListener(this);
        bhoraInicio.setOnClickListener(this);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();

        // obtiene id de fila de la tabla si se le ha pasado (hemos pulsado una nota para editarla)
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(DbAdapter.KEY_TABLES_ID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DbAdapter.KEY_TABLES_ID) : null;
        }

        // Si se le ha pasado un id (no era null) rellena el título y el cuerpo con los campos guardados en la BD
        // en caso contrario se dejan en blanco (editamos una nota nueva)
        if (mRowId != null && diaText == null) {
            Cursor note = dbAdapter.fetch_table(mRowId);
            nombreText.setText(note.getString(
                    note.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_NAME)));
            diaText.setText(note.getString(
                    note.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_DATE)));
            subtituloText.setText(note.getString(
                    note.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_SUBTITLE)));
            horaInicioText.setText(note.getString(
                    note.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_HOUR)));
        }
        else {
            // Recibe la fecha de calendario
            Bundle date_cal = getIntent().getExtras();
            String date = date_cal.getString("DATE");
            diaText.setText(date);
        }

    }

    // método para los selectores de fecha y hora
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bfecha:
                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                ano = c.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        diaText.setText(dayOfMonth + " / " + (month+1) + " / " + year);
                    }
                },
                        ano,mes,dia);
                datePickerDialog.getDatePicker();
                datePickerDialog.show();
                break;
            case R.id.bhoraInicio:
                final Calendar c1 = Calendar.getInstance();
                horaInicio = c1.get(Calendar.HOUR_OF_DAY);
                minutosInicio = c1.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        horaInicioText.setText(hourOfDay + " : " + minute);
                    }
                },
                        horaInicio, minutosInicio,true);
                timePickerDialog1.show();
                break;
        }
    }


    public void saveTable(View view) {

        /*


        if (mRowId == null) {
            long id = dbAdapter.createtable(name, day, subtitle, hour);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            dbAdapter.updateTable(mRowId, name, day, subtitle, hour);

        }
        setResult(RESULT_OK);

         */

        Intent intent = new Intent (Activity_add_table.this, Activity_tables.class);
        startActivity(intent);
        finish();
        /*
        Cursor cursorAllTables = dbAdapter.fetch_exercises_in_table(nombreText.getText().toString());
        if(cursorAllTables==null){
            Toast.makeText(this, "No se puede crear una tabla vacia!", Toast.LENGTH_LONG).show();

        }else {
            Toast.makeText(this, "Tabla creada!", Toast.LENGTH_LONG).show();
        }

         */
        Toast.makeText(this, "Tabla creada!", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se recrea el menu que aparece en ActionBar de la actividad.
        getMenuInflater().inflate(R.menu.menu_add_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona la seleccion de opciones en el menú
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            if (mRowId != null) {
                dbAdapter.deleteNote(mRowId);
            }
            setResult(RESULT_OK);
            finish();
        }

        if (id == R.id.action_about) {
            System.out.println("APPMOV: About action...");
        }

        return super.onOptionsItemSelected(item);
    }

    public void addExercise(View view){

        Intent intent = new Intent (Activity_add_table.this, Activity_add_exercises.class);


        String name = nombreText.getText().toString();
        String day = diaText.getText().toString();
        String subtitle = subtituloText.getText().toString();
        String hour = horaInicioText.getText().toString();


        if (mRowId == null) {
            long id = dbAdapter.createtable(name, day, subtitle, hour);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            dbAdapter.updateTable(mRowId, name, day, subtitle, hour);

        }
        setResult(RESULT_OK);

        // obtiene referencia a los 4 views que componen el layout de esta clase y los botones
        nombreText = (EditText) findViewById(R.id.name_table_add_table);
        diaText = (EditText) findViewById(R.id.dia_tabla);
        horaInicioText = (EditText) findViewById(R.id.hora_inicio_tabla);
        subtituloText = (EditText) findViewById(R.id.subtitulo_tabla);



        //Añadimos los bundels necesarios que en este caso se corresponde con el id de table

        Cursor cursor_id_table_max = dbAdapter.fetch_id_tableMax();
        long id_tableMax = cursor_id_table_max.getLong(cursor_id_table_max.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_ID));

        EditText name_table = (EditText) findViewById(R.id.name_table_add_table);
        String nameTable = name_table.getText().toString();


        String activity = "add_table";
        Bundle date_cal = getIntent().getExtras();
        String date = diaText.getText().toString();
        /*
        String title_table = nombreText.getText().toString();
        String hour_init = horaInicioText.getText().toString();
        String subtitle_table= subtituloText.getText().toString();
         */



        Bundle category = new Bundle();

        category.putLong("ID", id_tableMax);
        category.putString("ACTIVITY", activity);
        category.putString("DATE", date);
        /*
        category.putString("TITLE_TABLE", title_table);
        category.putString("HOUR", hour_init);
        category.putString("SUBTITLE", subtitle_table);

         */


        intent.putExtras(category);

        startActivity(intent);

    }

    public void onBackPressed(){
        Intent intent = new Intent (Activity_add_table.this, Activity_tables.class);
        startActivity(intent);
    }

}

