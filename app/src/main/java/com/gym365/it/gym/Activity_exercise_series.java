package com.gym365.it.gym;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import pl.droidsonroids.gif.GifImageView;
//import android.support.v7.app.AppCompatActivity;

public class Activity_exercise_series extends AppCompatActivity {

    private DbAdapter dbAdapter;
    private Long mRowId;
    private ListView m_listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_series);


        Bundle select_name_exercise = getIntent().getExtras();
        Bundle select_id_table = getIntent().getExtras();
        long id_table = select_id_table.getLong("ID");
        String name_exercise = select_name_exercise.getString("NAME_EXERCISE");
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");
        Bundle select_name_table = getIntent().getExtras();
        String name_table_add_table = select_name_table.getString("NAME_TABLE");


        TextView title_exercise = (TextView) findViewById(R.id.name_exerciise_with_serie);
        title_exercise.setText(name_exercise);

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();

        String name_table = "";
        if (activity.equals("tables")){

            Cursor cursor_name_table = dbAdapter.fetch_table(id_table);
            name_table = cursor_name_table.getString(cursor_name_table.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_NAME));
            TextView tittle_table = (TextView) findViewById(R.id.name_table_with_serie);
            tittle_table.setText(name_table);
        } else if (activity.equals("add_table")){
            TextView tittle_table = (TextView) findViewById(R.id.name_table_with_serie);
            tittle_table.setText(name_table_add_table);
        }
        String name_exercise_cursor = "'"+name_exercise+"'";
        Cursor cursor_photo = dbAdapter.fetch_exercise_ID_byName(name_exercise_cursor);
        //Este código permite extraer una ruta de la carpeta drawable de la base de datos y lo está introduciendo en gifview
        final String uri_photo = "@drawable/"+String.valueOf(cursor_photo.getString(cursor_photo.getColumnIndexOrThrow(DbAdapter.KEY_PHOTO)));
        int imageResource = getResources().getIdentifier(uri_photo, null, getPackageName());
        GifImageView image_dialog_exercise = (GifImageView) findViewById(R.id.gif_exercise_with_serie);
        image_dialog_exercise.setImageResource(imageResource);



         m_listview = (ListView) findViewById(R.id.n_serie);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void fillData(long id_item) {
        Cursor cursor_serie = dbAdapter.fetch_serie(id_item);
        if (cursor_serie!=null) {
            // Creamos un array con los campos que queremos mostrar en el listview (sólo el título de la nota)
            String[] from = new String[]{DbAdapter.KEY_SERIE_SERIE, DbAdapter.KEY_SERIE_REPES, DbAdapter.KEY_SERIE_WEIGHT};

            // array con los campos que queremos ligar a los campos del array de la línea anterior (en este caso sólo text1)
            int[] to = new int[]{R.id.serie, R.id.repetitions, R.id.weight};

            // Creamos un SimpleCursorAdapter y lo asignamos al listview para mostrarlo
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.list_series, cursor_serie, from, to, 0);
            m_listview.setAdapter(notes);
        }
    }


    public void Onclick_add_new_serie (View view) {
        showdialog(view);
    }

    public void showdialog(final View view){


        //Para tener acceso a los BottomSheetDialog, ha habido que añadir al Grandle esta implementación: implementation 'com.android.support:design:28.0.0'
        final BottomSheetDialog dialog_series= new BottomSheetDialog(this);
        View view_dialog=getLayoutInflater().inflate(R.layout.dialog_serie,null);
        dialog_series.setContentView(view_dialog);

        dialog_series.show();


        //ahora añadimos el nombre del ejercicio al dialog
        Bundle select_name_exercise = getIntent().getExtras();
        String name_exercise = select_name_exercise.getString("NAME_EXERCISE");
        TextView title_exercise = (TextView) dialog_series.findViewById(R.id.dialog_exercise_title);
        title_exercise.setText(name_exercise);

        //en esta parte del codigo agregaos un mínimo y un máximo al numberPickers.
        final NumberPicker numberPicker = view_dialog.findViewById(R.id.numberPickers);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(100);


        final NumberPicker numberPicker2 = view_dialog.findViewById(R.id.numberPickers2);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(100);


        //funciń para que cuando pinches el boton del dialog se agrege una nuveva serie a la base de datos
        Button boton_con_serie= (Button) view_dialog.findViewById(R.id.button_add_serie);
        boton_con_serie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Bundle select_id_table = getIntent().getExtras();
                long id_table = select_id_table.getLong("ID");

                Bundle select_id_exercise = getIntent().getExtras();
                long id_exercise = select_id_exercise.getLong("ID_EXERCISE");*/

                Cursor cursor_id_exercise_and_table = dbAdapter.fetch_id_tabla_with_exercise();
                long id_exercise_and_table = cursor_id_exercise_and_table.getLong(cursor_id_exercise_and_table.getColumnIndexOrThrow(DbAdapter.KEY_T_ID));
                Cursor cursor_id_serieMax = dbAdapter.fetch_id_serieMax_to_id_item(id_exercise_and_table);
                long idSerieMax = 0;

                if(cursor_id_serieMax == null) {
                    idSerieMax = 1;
                }else{
                    idSerieMax = cursor_id_serieMax.getLong(cursor_id_serieMax.getColumnIndexOrThrow(DbAdapter.KEY_SERIE_SERIE))+1;
                }

                long number_repetition = numberPicker.getValue();
                long number_kilos = numberPicker2.getValue();

                dbAdapter.addSerie(number_repetition, number_kilos, id_exercise_and_table, idSerieMax);
                fillData(id_exercise_and_table);
                dialog_series.dismiss();
            }
         });
    }

    public void Onclick_confrim (View view) {
        Intent intent = new Intent (Activity_exercise_series.this, Activity_add_category_exercise.class);

        Bundle select_Id = getIntent().getExtras();
        long category_id = select_Id.getLong("ID");
        Bundle select_category = getIntent().getExtras();
        String category_string = select_category.getString("CATEGORY");
        Bundle select_activity = getIntent().getExtras();
        String activity = select_activity.getString("ACTIVITY");

        Bundle category = new Bundle();
        category.putString("CATEGORY",category_string );
        category.putString("ACTIVITY",activity );
        category.putLong("ID",category_id);
        intent.putExtras(category);

        startActivity(intent);

    }

}
