package com.gym365.it.gym;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import android.support.v7.app.AppCompatActivity;
public class Activity_savedroutes extends AppCompatActivity {

    private static final String TAG = "APMOV: DbAdapter";

    private DbAdapter dbAdapter;
    private ListView list_routes;

    private static final String DATABASE_NAME = "data";
    private static final String PRELOADED_DATABASE_NAME = "preloaded.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // se infla el layout
        setContentView(R.layout.activity_savedroutes);

        list_routes = (ListView) findViewById(R.id.saved_routes);

        fillData();

        // Pulsar la ruta
        list_routes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        Log.w(TAG, "HOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+id);
                        Intent intent = new Intent (Activity_savedroutes.this, Activity_savedroutesmap.class);

                        // Pasamos el id de la ruta para dibujarla en el mapa
                        Bundle b = new Bundle();
                        b.putLong("ROUTE", id);
                        intent.putExtras(b);

                        startActivity(intent);
                    }
                }
        );

        // Mantener pulsada la ruta
        list_routes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id)
            {
               deleteRoute(id);
               return  false;
            }
        }
        );

    }

    // Eliminar la ruta de la BD
    public void deleteRoute(final long id) {

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.dialog_title_remove_route);
        ad.setMessage(R.string.message_dialog_remove_route);
        ad.setIcon(R.drawable.icono_basura);
        ad.setPositiveButton(R.string.dialog_buttonok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dbAdapter.delete_coordinates(id);
                        dbAdapter.delete_route(id);
                        fillData();
                        dialog.cancel();
                    }
                });
        ad.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ad.show();

    }

    // Rellenar el ListView con las rutas que haya en la BD
    private void fillData() {
        Cursor routesCursor = dbAdapter.fetchAllRoutes();
        if (routesCursor!=null) {

            // Creamos un array con los campos que queremos mostrar en el listview
            String[] from = new String[]{DbAdapter.KEY_ALL_ROUTES_NAME};

            // array con los campos que queremos ligar a los campos del array de la línea anterior
            int[] to = new int[]{R.id.routesname};

            // Creamos un SimpleCursorAdapter y lo asignamos al listview para mostrarlo
            SimpleCursorAdapter routes =
                    new SimpleCursorAdapter(this, R.layout.routes_name, routesCursor, from, to, 0);

            list_routes.setAdapter(routes);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
