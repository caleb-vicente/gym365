package com.gym365.it.gym;


import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class Activity_category_exercise extends AppCompatActivity {

    private DbAdapter dbAdapter;
    private ListView m_listview;

    private static final String DATABASE_NAME = "data";
    private static final String PRELOADED_DATABASE_NAME = "preloaded.db";



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
        setContentView(R.layout.activity_category_exercise);

        ImageView img= (ImageView) findViewById(R.id.image_activity_list_exercise);

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
        m_listview = (ListView) findViewById(R.id.id_list_view);

        // rellenamos el listview con los títulos de todas las notas en la BD
        fillData(category_query);

        //cuando se pulsa en uno de las tablas se accede a un dialog, pasándole como parametro la tabla sobre la que se ha pulsado
        m_listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
                    {
                        TextView titleview = view.findViewById(R.id.id_name_exercise_activity_add);
                        String name = titleview.getText().toString();
                        Log.i("TAG", "EL IDD EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEES:"+titleview.getText());
                        showdialog(name);

                    }
                }
        );
    }

    private void fillData(String category) {

        /*En este código se extrae coge un cursor con toda la información necesaria sobre los ejercicios de la categoría,
        * pero se modifica la columna de las imágenes creando un ArrayList, que será el que luego se adaptará al ListView*/
        Cursor notesCursor = dbAdapter.fetch_exercise(category);

        int indexPhoto = notesCursor.getColumnIndexOrThrow(DbAdapter.KEY_PHOTO);
        int indexCategory = notesCursor.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORY);
        int indexName = notesCursor.getColumnIndexOrThrow(DbAdapter.KEY_NAME);

        ArrayList<HashMap<String, String>> miArrayList = new ArrayList<>();
        while(notesCursor.moveToNext()) {
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

            final ListAdapter nameAdapter = new SimpleAdapter(this, miArrayList, R.layout.list_exercises, new String[] {
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

    public void showdialog(String name){

        /*Con esta función invocada desde tables_name.xml se esta creando un dialog
         * que te permite seleccionar si se desea ver los ejercicios que contienen la tabla
         * o se prefiere añadir ejercicios a la misma*/

        //Para tener acceso a los BottomSheetDialog, ha habido que añadir al Grandle esta implementación: implementation 'com.android.support:design:28.0.0'
        BottomSheetDialog dialog_table = new BottomSheetDialog(this);
        View view_dialog=getLayoutInflater().inflate(R.layout.dialog_exercise_without_chronometer,null);
        dialog_table.setContentView(view_dialog);


        dialog_table.show();

        /*
        En esta parte del código se accede a los id de los elementos del dialog y se les añade el texto, tanto de
        el título de la tabla como el subtitulo del ejercicio
         */

        TextView dialog_title = (TextView) dialog_table.findViewById(R.id.dialog_exercise_title_without_chronometer);
        TextView dialog_subtitle = (TextView) dialog_table.findViewById(R.id.dialog_exercise_subtitle_without_chronometer);

        final Cursor cursor_dialog_tables = dbAdapter.fetch_exercise_byName(name);

        final String title=cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_NAME));
        dialog_title.setText(title);

        dialog_subtitle.setText(cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORY)));




        //Este código permite extraer una ruta de la carpeta drawable de la base de datos.
        String uri = "@drawable/"+String.valueOf(cursor_dialog_tables.getString(cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_PHOTO)));
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        ImageView image_dialog_exercise = (ImageView) dialog_table.findViewById(R.id.exercise_photo_dialog);
        image_dialog_exercise.setImageResource(imageResource);



    }


}