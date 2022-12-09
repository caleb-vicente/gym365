package com.gym365.it.gym;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import pl.droidsonroids.gif.GifImageView;


public class Activity_add_category_exercise extends AppCompatActivity {

    private DbAdapter dbAdapter;
    private ListView m_listview;

    private static final String DATABASE_NAME = "data";
    private static final String PRELOADED_DATABASE_NAME = "preloaded.db";

    private TextView name_exercise;
    private Long mRowId;
    private static final String TAG = "APMOV: DbAdapter"; // Usado en los mensajes de Log

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //En este Bundle estamos recogiendo la selección de la categoría del ejercicio seleccionada en Activity_exercises.java
        Bundle select_category = getIntent().getExtras();
        String category = select_category.getString("CATEGORY");
        String category_query= "'"+category+"'";


        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();

        // Lo siguiente se ejecuta sólo la primera vez que se instala la
        // aplicación.
        // Si no existe la base de datos, la copiamos del directorio assets
        SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
        Boolean firstTime = mPreferences.getBoolean("firstTime", true);
        if (firstTime) {
            try {
                String destPath = "/data/data/" + getPackageName() + "/databases/" + DATABASE_NAME;

                System.out.println("Traza: no existe el fichero");
                InputStream in = getAssets().open(PRELOADED_DATABASE_NAME);
                OutputStream out = new FileOutputStream(destPath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.flush();
                out.close();
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //inflamos el layout
        setContentView(R.layout.activity_add_category_exercise);

        ImageView img= (ImageView) findViewById(R.id.image_activity_list_exercise2);

        switch (category) {
            case "pecho":
                img.setImageResource(R.drawable.imagen_pecho);
                break;
            case "biceps":
                img.setImageResource(R.drawable.imagen_biceps);
                break;
            case "triceps":
                img.setImageResource(R.drawable.imagen_triceps);
                break;
            case "espalda":
                img.setImageResource(R.drawable.imagen_espalda);
                break;
            case "cardio":
                img.setImageResource(R.drawable.imagen_cardio);
                break;
            case "pierna":
                img.setImageResource(R.drawable.imagen_piernas);
                break;
            case "abdominal":
                img.setImageResource(R.drawable.imagen_abdominal);
                break;
            case "hombro":
                img.setImageResource(R.drawable.imagen_hombro);
                break;
        }





        // Creamos un listview que va a contener el título de todas las notas y
        // en el que cuando pulsemos sobre un título lancemos una actividad de editar
        // la nota con el id correspondiente
        m_listview = (ListView) findViewById(R.id.id_list_view2);

        // rellenamos el listview con los títulos de todas las notas en la BD
        fillData(category_query);


    }

    private void fillData(String category) {
        /*En este código se extrae coge un cursor con toda la información necesaria sobre los ejercicios de la categoría,
         * pero se modifica la columna de las imágenes creando un ArrayList, que será el que luego se adaptará al ListView*/
        Cursor notesCursor = dbAdapter.fetch_exercise(category);

        int indexPhoto = notesCursor.getColumnIndexOrThrow(DbAdapter.KEY_PHOTO);
        int indexCategory = notesCursor.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORY);
        int indexName = notesCursor.getColumnIndexOrThrow(DbAdapter.KEY_NAME);

        ArrayList<HashMap<String, String>> miArrayList = new ArrayList<>();
        while(notesCursor.moveToNext()){
            // miArrayList.add(notesCursor.getString(tituloIndex)); // ó idIndex
            String id_photo = Integer.toString(getResources().getIdentifier(notesCursor.getString(indexPhoto), "drawable", getPackageName()));
            String name = notesCursor.getString(indexName);
            String category_cursor = notesCursor.getString(indexCategory);
            HashMap<String, String> map=new HashMap<String, String>();
            map.put(dbAdapter.KEY_PHOTO,id_photo);
            map.put(dbAdapter.KEY_NAME,name);
            map.put(dbAdapter.KEY_CATEGORY,category_cursor);
            miArrayList.add(map);
        }


        if (notesCursor!=null) {

            final ListAdapter nameAdapter = new SimpleAdapter(this, miArrayList, R.layout.list_exercise_add_exercise, new String[] {
                    dbAdapter.KEY_PHOTO, dbAdapter.KEY_NAME, dbAdapter.KEY_CATEGORY}, new int[] { R.id.photo , R.id.id_name_exercise_activity_add, R.id.category});

            m_listview.setAdapter(nameAdapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se recrea el menu que aparece en ActionBar de la actividad.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    */

    public void add_one_exercise (View view) {
        showdialog(view);

        /*
        setResult(RESULT_OK);
        Intent intent = new Intent (Activity_add_category_exercise.this, Activity_add_category_exercise.class);
        startActivity(intent);
        finish();
        */
    }
    public void showdialog(final View view){
        /*Con esta función invocada desde list_exercise_add_table.xml se esta creando un dialog
         * que te permite seleccionar si deseas añadir un ejercicio con o sin series*/


        //Para tener acceso a los BottomSheetDialog, ha habido que añadir al Grandle esta implementación: implementation 'com.android.support:design:28.0.0'
        final BottomSheetDialog dialog_table = new BottomSheetDialog(this);
        final View view_dialog=getLayoutInflater().inflate(R.layout.dialog_exercise_add_table,null);
        dialog_table.setContentView(view_dialog);



        dialog_table.show();


        final Bundle select_Id = getIntent().getExtras();
        final long id_table = select_Id.getLong("ID");

        //Se proceden a rellenar los textview e imageview del dialog
        //-----------------------------------------------------------------------------------------------------
        LinearLayout vwParentRow = (LinearLayout)view.getParent();
        name_exercise = (TextView) vwParentRow.findViewById(R.id.id_name_exercise_activity_add);
        final String name = name_exercise.getText().toString();


        TextView title_exercise = (TextView) view_dialog.findViewById(R.id.dialog_add_exercise_title_table);
        title_exercise.setText(name);

        Cursor cursor_photo = dbAdapter.fetch_exercise_byName(name);

        //Este código permite extraer una ruta de la carpeta drawable de la base de datos y lo está introduciendo en gifview
        final String uri_photo = "@drawable/"+String.valueOf(cursor_photo.getString(cursor_photo.getColumnIndexOrThrow(DbAdapter.KEY_PHOTO)));
        int imageResource = getResources().getIdentifier(uri_photo, null, getPackageName());
        GifImageView image_dialog_exercise = (GifImageView) view_dialog.findViewById(R.id.add_exercise_photo_dialog_table);
        image_dialog_exercise.setImageResource(imageResource);
        //-----------------------------------------------------------------------------------------------------

        //Implementación de la funcionalidad botón sin series
        //-----------------------------------------------------------------------------------------------------
        Button boton_sin_serie= (Button) view_dialog.findViewById(R.id.button_add_exercise_sin_serie);
        boton_sin_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout vwParentRow = (LinearLayout)view.getParent();
                Bundle select_Id = getIntent().getExtras();
                long id_table = select_Id.getLong("ID");
                name_exercise = (TextView) vwParentRow.findViewById(R.id.id_name_exercise_activity_add);
                String name = name_exercise.getText().toString();
                String name_query = "'"+name+"'";

                Cursor cursor_id_exercise = dbAdapter.fetch_exercise_ID_byName(name_query);
                long id_exercise = cursor_id_exercise.getLong(cursor_id_exercise.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));


                if (mRowId == null) {
                    long nFila = dbAdapter.addExercise(id_table,id_exercise);

                    if (nFila > 0) {
                        mRowId = nFila;
                    }
                } else {
                    dbAdapter.updateTable_AddExercise(mRowId, id_table);

                }
                finish();

                Intent intent = new Intent (Activity_add_category_exercise.this, Activity_add_category_exercise.class);

                Bundle select_category = getIntent().getExtras();
                String category_string = select_category.getString("CATEGORY");
                Bundle select_activity = getIntent().getExtras();
                String activity = select_activity.getString("ACTIVITY");
                Bundle select_name_table = getIntent().getExtras();
                String name_table  = select_name_table.getString("NAME_TABLE");

                long category_int= id_table;
                Bundle category = new Bundle();
                category.putLong("ID", category_int);
                category.putString("CATEGORY",category_string );
                category.putString("ACTIVITY",activity);
                category.putString("NAME_TABLE",name_table);
                intent.putExtras(category);

                startActivity(intent);


            }
        });

        Button boton_con_serie= (Button) view_dialog.findViewById(R.id.button_add_exercise_con_serie);
        boton_con_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Activity_add_category_exercise.this, Activity_exercise_series.class);

                String name_query = "'"+name+"'";
                Cursor cursor_id_exercise = dbAdapter.fetch_exercise_ID_byName(name_query);
                long id_exercise = cursor_id_exercise.getLong(cursor_id_exercise.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));


                if (mRowId == null) {
                    long nFila = dbAdapter.addExercise(id_table,id_exercise);

                    if (nFila > 0) {
                        mRowId = nFila;
                    }
                } else {
                    dbAdapter.updateTable_AddExercise(mRowId, id_table);

                }

                Bundle select_category = getIntent().getExtras();
                String category_string = select_category.getString("CATEGORY");
                Bundle select_activity = getIntent().getExtras();
                String activity = select_activity.getString("ACTIVITY");
                Bundle select_name_table = getIntent().getExtras();
                String name_table  = select_name_table.getString("NAME_TABLE");


                Bundle category = new Bundle();
                category.putString("NAME_EXERCISE", name);
                category.putLong("ID_EXERCSE", id_exercise);
                category.putLong("ID", id_table);
                category.putString("CATEGORY",category_string );
                category.putString("ACTIVITY",activity );
                category.putString("NAME_TABLE",name_table);
                intent.putExtras(category);

                startActivity(intent);
            }
        });

    }


    public void onBackPressed(){
        Intent intent = new Intent (Activity_add_category_exercise.this, Activity_add_exercises.class);

        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_Id = getIntent().getExtras();
        long id_table = select_Id.getLong("ID");
        Bundle category = new Bundle();
        category.putLong("ID", id_table);
        category.putString("ACTIVITY", activity);

        intent.putExtras(category);

        startActivity(intent);


    }




}