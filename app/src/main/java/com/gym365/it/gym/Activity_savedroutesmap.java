package com.gym365.it.gym;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class Activity_savedroutesmap extends FragmentActivity implements OnMapReadyCallback {

    // Nuestro mapa
    GoogleMap mMap;

    private DbAdapter dbAdapter;
    private Long id_route;

    private static final String DATABASE_NAME = "data";
    private static final String PRELOADED_DATABASE_NAME = "preloaded.db";

    // tipos de línea
    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    // para perdir permiso de localización
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();

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

        setContentView(R.layout.activity_routesmap);

        // Bundle para obtener datos de la anterior clase
        Bundle bundle = getIntent().getExtras();
        id_route = bundle.getLong("ROUTE");

        // Pedimos el objeto GoogleMap de forma asíncrona:
        // lo recibiremos en OnMapReadyCallback.onMapReady()
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfrag);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    // Único método de la interfaz OnMapReadyCallback, que implementa nuestra Activity,
    // que debemos rellenar para hacer uso del mapa una vez que está listo
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Obtenemos el mapa
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); // MAPA TIPO

        // Primero pedimos el objeto que nos permite modificar la UI.
        // Todos los cambios efectuados sobre este objeto se reflejan inmediatamente en el mapa
        UiSettings settings = mMap.getUiSettings();

        settings.setZoomControlsEnabled(true); // botones para hacer zoom

        settings.setCompassEnabled(true); // brújula (sólo se muestra el icono si se rota el mapa con los dedos)

        // Comprobamos si tenemos permiso para acceder a la localización
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // botón "My Location"
        } else {
            // no tiene permiso, solicitarlo
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
            // cuando se nos conceda el permiso se llamará a onRequestPermissionsResult()
        }

        // Mostrar la ruta seleccionada y centrada
        mostrarRuta(id_route);

    }

    private void mostrarRuta(long id) {

        Cursor r = dbAdapter.fetch_route(id);

        int indexLat = r.getColumnIndexOrThrow(DbAdapter.KEY_LATITUDE);
        int indexLng = r.getColumnIndexOrThrow(DbAdapter.KEY_LONGITUDE);

        // Inicializamos unos arrays del tamaño del cursor
        int sizeCursor = r.getCount();
        double[] latitudes = new double[sizeCursor];
        double[] longitudes = new double[sizeCursor];

        // Guardamos las coordenas de la ruta en arrays
        int count = 0;
        do {
            latitudes[count] = r.getDouble(indexLat);
            longitudes[count] = r.getDouble(indexLng);
            count++;
        } while(r.moveToNext());

        // Se centra el mapa en la primera coordenada de la ruta
        centerMap(latitudes[0], longitudes[0]);

        //Dibujo con Lineas
        PolylineOptions lineas = new PolylineOptions();

        // Se van dibujando los trazos entre coordenadas hasta la última
        for (int j=0; j<latitudes.length; j++) {
            lineas.add(new LatLng(latitudes[j], longitudes[j]));
        }
                    lineas.width(11)          // TAMAÑO TRAZO
                    .color(Color.RED)         // COLOR TRAZO
                    .pattern(PATTERN_DASHED); // TIPO TRAZO
            mMap.addPolyline(lineas);
    }

    public void centerMap(double latitude, double longitude){

        // A partir de una pareja de coordenadas (tipo double) creamos un objeto LatLng,
        // que es el tipo de dato que debemos usar al tratar con mapas
        LatLng position = new LatLng(latitude, longitude);

        float zoom = 14;
        float angulo = 80;

        // Cambiar posición de la cámara
        CameraPosition camPos = new CameraPosition.Builder()
                .target(position)
                .zoom(zoom)
                .tilt(angulo)
                .build();

        CameraUpdate update = CameraUpdateFactory.newCameraPosition(camPos);

        // Pasamos el tipo de actualización configurada al método del mapa que mueve la cámara
        mMap.moveCamera(update);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    finish();
                    startActivity(new Intent(this, this.getClass()));

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

}