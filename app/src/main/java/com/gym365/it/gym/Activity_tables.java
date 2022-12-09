package com.gym365.it.gym;




import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

//import android.support.design.widget.BottomSheetDialog;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Activity_tables extends AppCompatActivity {

    private static final String TAG = "APMOV: DbAdapter";

    private DbAdapter dbAdapter;
    private ListView m_listview;

    private Long mRowId;

    private static final String DATABASE_NAME = "data";
    private static final String PRELOADED_DATABASE_NAME = "preloaded.db";

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    SimpleCursorAdapter onQueryText_table = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setContentView(R.layout.activity_tables);

        // Creamos un listview que va a contener el título de todas las notas y
        // en el que cuando pulsemos sobre un título lancemos una actividad de editar
        // la nota con el id correspondiente
        m_listview = (ListView) findViewById(R.id.id_list_tables);

        // rellenamos el listview con los títulos de todas las notas en la BD

        onQueryText_table = fillData();

        //cuando se pulsa en uno de las tablas se accede a un dialog, pasándole como parametro la tabla sobre la que se ha pulsado
        m_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                  @Override
                                                  public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id)
                                                  {
                                                      showdialog(id);
                                                      return false;
                                                  }
                                              }
        );

        m_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent (Activity_tables.this, Activity_list_table.class);


                Cursor cursor_title_tables = dbAdapter.fetch_table(id);
                String title=cursor_title_tables.getString(cursor_title_tables.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_NAME));





                //Bundle que le "pasa la información del del id o del nombre de la tabla, a la actividad de Activity_list_table"
                Bundle tabla = new Bundle();
                tabla.putString("TABLA", title);
                tabla.putLong("ID",id);
                intent.putExtras(tabla);


                startActivity(intent);

            }
        });

    }

    private SimpleCursorAdapter fillData() {
        Cursor tablesCursor = dbAdapter.fetchAllTables();
        SimpleCursorAdapter tables = null;
        if (tablesCursor!=null) {

            // Creamos un array con los campos que queremos mostrar en el listview (sólo el título de la nota)
            String[] from = new String[]{DbAdapter.KEY_TABLES_NAME, DbAdapter.KEY_TABLES_SUBTITLE, DbAdapter.KEY_TABLES_DATE, DbAdapter.KEY_TABLES_HOUR};

            // array con los campos que queremos ligar a los campos del array de la línea anterior (en este caso sólo text1)
            int[] to = new int[]{R.id.tablesname, R.id.tablessubtitle, R.id.tablesdate, R.id.tableshour};

            // Creamos un SimpleCursorAdapter y lo asignamos al listview para mostrarlo
            tables =
                    new SimpleCursorAdapter(this, R.layout.tables_name, tablesCursor, from, to, 0);

            m_listview.setAdapter(tables);
        }
    return tables;
    }


    public void showdialog(final long mRowId){
        /*Con esta función invocada desde tables_name.xml se esta creando un dialog
        * que te permite seleccionar si se desea ver los ejercicios que contienen la tabla
        * o se prefiere añadir ejercicios a la misma*/


        //Para tener acceso a los BottomSheetDialog, ha habido que añadir al Grandle esta implementación: implementation 'com.android.support:design:28.0.0'
        final BottomSheetDialog dialog_table = new BottomSheetDialog(this);
        final View view_dialog=getLayoutInflater().inflate(R.layout.dialog_table,null);
        dialog_table.setContentView(view_dialog);

        dialog_table.show();

        /*
        En esta parte del código se accede a los id de los elementos del dialog y se les añade el texto, tanto de
        el título de la tabla como el subtitulo del ejercicio
         */

        TextView dialog_title = (TextView) dialog_table.findViewById(R.id.dialogtitle);
        TextView dialog_subtitle = (TextView) dialog_table.findViewById(R.id.dialogsubtitle);
        TextView dialog_date = (TextView) dialog_table.findViewById(R.id.dialogdate);
        TextView dialog_hour = (TextView) dialog_table.findViewById(R.id.dialoghour);

        final Cursor cursor_dialog_tables = dbAdapter.fetch_table(mRowId);

        final String title=cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_NAME));
        dialog_title.setText(title);

        dialog_subtitle.setText(cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_SUBTITLE)));

        dialog_date.setText(cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_DATE)));

        dialog_hour.setText(cursor_dialog_tables.getString(
                cursor_dialog_tables.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_HOUR)));


        /*En esta parte del cógigo se accede al botón que se encuentra dentro del dialog, para poder realizar
        * la función onclick que nos llevará a la siguiente actividad.*/
        Button boton_dialog= (Button) view_dialog.findViewById(R.id.buttonAddExerciseToTable);
        boton_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Activity_tables.this, Activity_add_exercises.class);
                String activity = "tables";
                long category_int= mRowId;
                Bundle category = new Bundle();
                category.putLong("ID", category_int);
                category.putString("ACTIVITY", activity);
                intent.putExtras(category);

                startActivity(intent);
            }
        });


        /*En esta parte, se extrae el layaut donde se encuentra definido el nombre de la table que es,
        * y si se pulsa en el mismo te manda a una actividad (Activit_list_table), que muestra todos los ejercicios
        * dentro de la tabla*/

        dialog_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Activity_tables.this, Activity_list_table.class);
                //Bundle que le "pasa la información del del id o del nombre de la tabla, a la actividad de Activity_list_table"

                long category_int= mRowId;
                Bundle tabla = new Bundle();
                tabla.putString("TABLA", title);
                tabla.putLong("ID", category_int);
                intent.putExtras(tabla);

                startActivity(intent);
            }
        });

        /*En esta parte del cógigo se accede al botón de eliminar tabla que se encuentra dentro del dialog, para poder realizar
         * la función onclick que nos llevará a la siguiente actividad.*/
        Button boton_remove_table_dialog= (Button) view_dialog.findViewById(R.id.buttonRemoveExerciseToTable);
        boton_remove_table_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_table(mRowId);
                dialog_table.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.w("HOLA", "HAAAAAAAAAAAAAAAAAAAAAAAAAAASSSSSSSSSSSSTTTTTTTTTTAAAAAAAAAAAA AQUÍ"+onQueryText_table);
                onQueryText_table.getFilter().filter(newText);
                return false;
            }
        });
    return true;
    }

    public void clickButtonAddTable(View view) {

        Intent intent = new Intent (Activity_tables.this, Activity_add_table.class);
        Bundle b = new Bundle();
        b.putString("DATE", "");
        intent.putExtras(b);
        startActivity(intent);

    }
    public void delete_table(final long id_table) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.dialog_title_remove_table);
        ad.setMessage(R.string.message_dialog_remove_table);
        ad.setIcon(R.drawable.icono_basura);
        ad.setPositiveButton(R.string.dialog_buttonok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dbAdapter.delete_all_exercises_from_table(id_table);
                        dbAdapter.delete_table(id_table);
                        fillData();
                        dialog.cancel();
                    }
                });
        ad.setNegativeButton(R.string.dialog_buttonCancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });

        ad.show();

    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    */

    public void onBackPressed(){
        Intent intent = new Intent (Activity_tables.this, MenuActivity.class);
        startActivity(intent);
    }
}