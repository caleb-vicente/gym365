package com.gym365.it.gym;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;

import static com.gym365.it.gym.R.drawable.imagen_cardio;

public class Activity_list_table extends AppCompatActivity {

    private DbAdapter dbAdapter;
    private ListView m_listview;
    private ListView m_listview_series;

    private static final String TAG = "APMOV: DbAdapter"; // Usado en los mensajes de Log
    long pause;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Aquí se debe detectar recoger el string del nombre de la tabla sobre la que se está pulsando con el bundle
        //Con ese string, ya se hará la busqueda en la base de datos y se elegirán todos los ejercicios pertenecientes a esa tabla
        Bundle bundle_name_table = getIntent().getExtras();
        String name_table= bundle_name_table.getString("TABLA");

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();


        //inflamos el layout
        setContentView(R.layout.activity_list_table);

        //Se escribe el nombre de la tabla en la parte superior del listado.
        TextView title_table_list = (TextView) findViewById(R.id.title_table_list);
        title_table_list.setText(name_table);

        /*Selecciona la imagen una vez que estamos dentro de la propia tabla, esto debera cambiar dependiendo
        * de en que tabla nos encontramos*/

        /*String uri = "drawable/imagen_hombro";

        int imageResource = R.drawable.icon;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        */
        int imageResource = R.drawable.wallpaper_table;
        ImageView image_table = (ImageView) findViewById(R.id.image_table);
        image_table.setImageDrawable(ResourcesCompat.getDrawable(getResources(), imageResource, null));


        // Creamos un listview que va a contener el título de todas las notas y
        // en el que cuando pulsemos sobre un título lancemos una actividad de editar
        // la nota con el id correspondiente
        m_listview = (ListView) findViewById(R.id.id_list_view_exercise_table);


        // rellenamos el listview con los títulos de todas las notas en la BD
        fillData(name_table);


        //cuando se pulsa en uno de las tablas se accede a un dialog, pasándole como parametro la tabla sobre la que se ha pulsado
        m_listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
                    {
                        TextView titleview = view.findViewById(R.id.title_exercise);
                        String name = titleview.getText().toString();
                        Bundle select_Id = getIntent().getExtras();
                        String name_query = "'"+name+"'";
                        Cursor cursor_id_exercise = dbAdapter.fetch_exercise_ID_byName(name_query);
                        long id_allexercise = cursor_id_exercise.getLong(cursor_id_exercise.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));
                        long id_table = select_Id.getLong("ID");
                        Cursor cursor_id_exercise_and_table = dbAdapter.fetch_id_tables(id_table,id_allexercise);
                        long id_item = cursor_id_exercise_and_table.getLong(cursor_id_exercise_and_table.getColumnIndexOrThrow(DbAdapter.KEY_T_ID));



                        showdialog(name);
                        fillData_series(id_item);

                    }
                }
        );



    }

    private void fillData_series(long id_item) {
        Cursor cursor_serie = dbAdapter.fetch_serie(id_item);
        if (cursor_serie!=null) {
            // Creamos un array con los campos que queremos mostrar en el listview (sólo el título de la nota)
            String[] from = new String[]{DbAdapter.KEY_SERIE_SERIE, DbAdapter.KEY_SERIE_REPES, DbAdapter.KEY_SERIE_WEIGHT};

            // array con los campos que queremos ligar a los campos del array de la línea anterior (en este caso sólo text1)
            int[] to = new int[]{R.id.serie, R.id.repetitions, R.id.weight};

            // Creamos un SimpleCursorAdapter y lo asignamos al listview para mostrarlo
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.list_series, cursor_serie, from, to, 0);
            m_listview_series.setAdapter(notes);
        }
    }

    private void fillData(String name_table) {

        Cursor cursor_exercise_in_table = dbAdapter.fetch_exercises_in_table(name_table);
        int indexPhoto = cursor_exercise_in_table.getColumnIndexOrThrow(DbAdapter.KEY_PHOTO);
        int indexCategory = cursor_exercise_in_table.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORY);
        int indexName = cursor_exercise_in_table.getColumnIndexOrThrow(DbAdapter.KEY_NAME);


        ArrayList<HashMap<String, String>> miArrayList = new ArrayList<>();


         do {
             // miArrayList.add(notesCursor.getString(tituloIndex)); // ó idIndex
             String id_photo = Integer.toString(getResources().getIdentifier(cursor_exercise_in_table.getString(indexPhoto), "drawable", getPackageName()));
             String name = cursor_exercise_in_table.getString(indexName);
             String category_cursor = cursor_exercise_in_table.getString(indexCategory);
             HashMap<String, String> map = new HashMap<String, String>();
             map.put(dbAdapter.KEY_PHOTO, id_photo);
             map.put(dbAdapter.KEY_NAME, name);
             map.put(dbAdapter.KEY_CATEGORY, category_cursor);
             miArrayList.add(map);
         } while(cursor_exercise_in_table.moveToNext());


        if (cursor_exercise_in_table!=null) {

            final ListAdapter nameAdapter = new SimpleAdapter(this, miArrayList, R.layout.new_item_list, new String[] {
                    dbAdapter.KEY_PHOTO, dbAdapter.KEY_NAME, dbAdapter.KEY_CATEGORY}, new int[] { R.id.photo , R.id.title_exercise, R.id.category_exercise});

            m_listview.setAdapter(nameAdapter);

        }
    }

        /*
        if (cursor_exercise_in_table!=null) {
            // Creamos un array con los campos que queremos mostrar en el listview (sólo el título de la nota)
            String[] from = new String[]{DbAdapter.KEY_NAME, DbAdapter.KEY_CATEGORY};

            // array con los campos que queremos ligar a los campos del array de la línea anterior (en este caso sólo text1)
            int[] to = new int[]{R.id.title_exercise, R.id.category_exercise};
            // Creamos un SimpleCursorAdapter y lo asignamos al listview para mostrarlo
            SimpleCursorAdapter adapter_exercise_in_table =
                    new SimpleCursorAdapter(this, R.layout.new_item_list, cursor_exercise_in_table, from, to, 0);

            m_listview.setAdapter(adapter_exercise_in_table);
        }
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se recrea el menu que aparece en ActionBar de la actividad.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void showdialog(final String name){
        /*Con esta función invocada desde tables_name.xml se esta creando un dialog
         * que te permite seleccionar si se desea ver los ejercicios que contienen la tabla
         * o se prefiere añadir ejercicios a la misma*/


        //Para tener acceso a los BottomSheetDialog, ha habido que añadir al Grandle esta implementación: implementation 'com.android.support:design:28.0.0'
        BottomSheetDialog dialog_table = new BottomSheetDialog(this);
        View view_dialog=getLayoutInflater().inflate(R.layout.dialog_exercise,null);
        dialog_table.setContentView(view_dialog);


        dialog_table.show();

        m_listview_series = (ListView) dialog_table.findViewById(R.id.series_exercise_in_table);



        /*
        En esta parte del código se accede a los id de los elementos del dialog y se les añade el texto, tanto de
        el título de la tabla como el subtitulo del ejercicio
         */

        TextView dialog_title = (TextView) dialog_table.findViewById(R.id.dialog_exercise_title);
        TextView dialog_subtitle = (TextView) dialog_table.findViewById(R.id.dialog_exercise_subtitle);



        final Cursor cursor_dialog_tables = dbAdapter.fetch_exercise_byName(name);

        final String title=cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_NAME));
        dialog_title.setText(title);

        dialog_subtitle.setText(cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_CATEGORY)));


        String uri = "@drawable/"+String.valueOf(cursor_dialog_tables.getString(cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_PHOTO)));
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        GifImageView image_dialog_exercise = (GifImageView) dialog_table.findViewById(R.id.exercise_photo_dialog);
        Log.w("HOLA", "HAAAAAAAAAAAAAAAAAAAAAAAAAAASSSSSSSSSSSSTTTTTTTTTTAAAAAAAAAAAA AQUÍ"+imageResource);
        image_dialog_exercise.setImageResource(imageResource);

        m_listview_series.setOnItemClickListener(
            new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
                {

                    TextView num_repes = (TextView) m_listview_series.findViewById(R.id.repetitions);
                    TextView num_weight = (TextView) m_listview_series.findViewById(R.id.weight);
                    String repes = num_repes.getText().toString();
                    String weight = num_weight.getText().toString();
                    long int_repes = Integer.parseInt(repes);
                    long int_weight = Integer.parseInt(weight);

                    Bundle select_Id = getIntent().getExtras();
                    String name_query = "'"+name+"'";
                    Cursor cursor_id_exercise = dbAdapter.fetch_exercise_ID_byName(name_query);
                    long id_allexercise = cursor_id_exercise.getLong(cursor_id_exercise.getColumnIndexOrThrow(DbAdapter.KEY_ROWID));
                    long id_table = select_Id.getLong("ID");

                    final Cursor cursor_id_item = dbAdapter.fetch_id_tables(id_table,id_allexercise);
                    long id_item = cursor_id_item.getLong(cursor_id_item.getColumnIndexOrThrow(DbAdapter.KEY_T_ID));
                    long serie = position +1;

                    final Cursor cursor_id_repes_weigth = dbAdapter.fetch_repes_weight(id);
                    long n_repes = cursor_id_repes_weigth.getLong(cursor_id_repes_weigth.getColumnIndexOrThrow(DbAdapter.KEY_SERIE_REPES));
                    long n_weight = cursor_id_repes_weigth.getLong(cursor_id_repes_weigth.getColumnIndexOrThrow(DbAdapter.KEY_SERIE_WEIGHT));

                    modificarSeies(id,id_item, serie, n_repes, n_weight);
                }
            }
        );

        //CRONOMETRO

        final Chronometer chronometer_dialog = (Chronometer) dialog_table.findViewById(R.id.chronometer);
        final ImageButton start_button =  dialog_table.findViewById(R.id.play);
        final ImageButton pause_button =  dialog_table.findViewById(R.id.pause);


        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer_dialog.setBase(SystemClock.elapsedRealtime());
                chronometer_dialog.start();
                start_button.setVisibility(View.INVISIBLE);
                pause_button.setVisibility(View.VISIBLE);
            }
        });
        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chronometer_dialog.stop();
                chronometer_dialog.setBase(SystemClock.elapsedRealtime());
                start_button.setVisibility(View.VISIBLE);
                pause_button.setVisibility(View.INVISIBLE);
            }
        });


    }
    public void  modificarSeies(final long id_serie, final long id_item, final long n_serie, final long repes, final long weight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_list_table.this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();




        View view = inflater.inflate(R.layout.dialog_edit_series, null);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_edit_series, null));

        final AlertDialog dialog_modificar_series = builder.create();
        dialog_modificar_series.show();




        final NumberPicker edit_repes = (NumberPicker) dialog_modificar_series.findViewById(R.id.edit_repes);
        final NumberPicker edit_weight = (NumberPicker) dialog_modificar_series.findViewById(R.id.edit_weight);
        final Button button_confirm = (Button) dialog_modificar_series.findViewById(R.id.confirm_news_serie);

        edit_repes.setMinValue(0);
        edit_weight.setMinValue(0);
        edit_repes.setMaxValue(200);
        edit_weight.setMaxValue(200);

        edit_repes.setValue((int) repes);
        edit_weight.setValue((int) weight);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long repes2 = edit_repes.getValue();
                long weight2 = edit_weight.getValue();
                dbAdapter.updateTable_AddSerie(id_serie,n_serie,repes2, weight2, id_item);
                Toast.makeText(getApplicationContext(),"Se han modificado los datos", Toast.LENGTH_LONG).show();
                fillData_series(id_item);
                dialog_modificar_series.dismiss();
            }
        });

    }



    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    */
}
