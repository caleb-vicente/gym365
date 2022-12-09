package com.gym365.it.gym;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Activity_newroute extends FragmentActivity implements OnMapReadyCallback {

    // Para la base de datos
    private String route_name = "";
    private Long rowid = null;
    private DbAdapter dbAdapter;

    // Nuestro mapa
    private GoogleMap mMap;

    // Ubicación en Leganés
    private double lat = 40.3320;
    private double lng = -3.7687;

    // Permiso para acceder a la localización
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;

    // Cuadro de diálogo al principio
    public void getAd() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.dialog_title);
        ad.setMessage(R.string.message_dialog);
        ad.setIcon(R.drawable.nueva_ruta);
        ad.setPositiveButton(R.string.dialog_buttonok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
        ad.show();
    }

    // Definimos nuestro escuchador de clicks en el mapa.
    private final GoogleMap.OnMapClickListener mClickListener = new GoogleMap.OnMapClickListener() {

        @Override
        public void onMapClick(LatLng position) {
            // Recibimos las coordenadas del punto del mapa que pulsó el usuario.
            // Para realizar la concatenación de texto forma eficiente, usaremos un objeto de la clase StringBuffer:
            StringBuffer text = new StringBuffer();
            text.append("GPS: ");
            text.append(position.latitude).append(", ").append(position.longitude);
            String coordText = text.toString();

            // Ponemos un marcador en dicha posición.
            MarkerOptions markerOpts = new MarkerOptions();
            markerOpts.position(position); // ubicación en el mapa (único requisito imprescindible)
            markerOpts.draggable(true); // se le permite ser arrastrado (¡preconfiguración!
            // para hacerlo a posteriori, utilizar Marker.setDraggable(boolean))
            // Se arrastra con una pulsación larga + movimiento sin levantar el dedo

            markerOpts.title("Establecer destino: pulsa en el icono de ruta"); // título
            markerOpts.snippet(coordText); // texto complementario al título

            Marker marker = mMap.addMarker(markerOpts);

        }
    }; //Fin de la definición e inicialización del atributo mClickListener

    // Ya que hemos hecho arrastrables los marcadores, creamos también un escuchador que
    // reaccione ante eventos de este tipo
    private final GoogleMap.OnMarkerDragListener mDragListener = new GoogleMap.OnMarkerDragListener() {

        @Override
        public void onMarkerDragStart(Marker marker) {
        }

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        // Sólo sobreescribimos este, que es el que nos interesa para conocer
        // la ubicación _final_ del marcador
        @Override
        public void onMarkerDragEnd(Marker marker) {

            LatLng position = marker.getPosition();

            // De nuevo, pasamos a texto las coordenadas del punto final
            // de forma eficiente, usando un objeto de la clase StringBuffer:
            StringBuffer text = new StringBuffer();
            text.append("GPS: ");
            text.append(position.latitude).append(", ").append(position.longitude);
            String coordText = text.toString();

            // Actualizamos el snippet del marcador
            marker.setSnippet(coordText);

        }
    }; //Fin de la definición e inicialización del atributo mDragListener

    // ---------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newroute);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();

        // Pedimos el objeto GoogleMap de forma asíncrona:
        // lo recibiremos en OnMapReadyCallback.onMapReady()
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfrag);
        mapFragment.getMapAsync(this);

    }

    // actualizar a las coordenadas actuales
    public void actualizarUbicacion(Location location) {
        if (location!=null) {
            lat = location.getLatitude();
            lng = location.getLongitude();

            // Si se ha puesto el nombre de la ruta y se ha creado en la BD:
            if (rowid != null) {
                    // Se van guardando las coordenadas en la BD
                    dbAdapter.putCoordinates(rowid, lat, lng);
            }
        }
    }
    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();

        //Puesto que la aplicación va a dejar de correr en el sistema, nos desuscribimos de las
        //actualizaciones
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // ---------------------------------------

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Mostrar diálogo del principio
        getAd();

        // Obtenemos el mapa
        mMap = googleMap;

        // En primer lugar, registramos los escuchadores de eventos del mapa que hemos creado
        mMap.setOnMapClickListener(mClickListener); // para clicks sobre el mapa
        mMap.setOnMarkerDragListener(mDragListener); // para eventos de arrastre de marcadores

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); // MAPA TIPO

        // Primero pedimos el objeto que nos permite modificar la UI.
        // Todos los cambios efectuados sobre este objeto se reflejan inmediatamente en el mapa
        UiSettings settings = mMap.getUiSettings();

        settings.setZoomControlsEnabled(true); // botones para hacer zoom

        settings.setCompassEnabled(true); // brújula (sólo se muestra el icono si se rota el mapa con los dedos)

        // Comprobamos si tenemos permiso para acceder a la localización
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); // botón "My Location"
        } else {
            // no tiene permiso, solicitarlo
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
            // cuando se nos conceda el permiso se llamará a onRequestPermissionsResult()
        }

        // Dadas unas coordenadas, centramos el mapa en ellas
        centerMap(lat, lng);

        actualizarRuta();

    }

    private void actualizarRuta() {

        // Comprobamos si tenemos permiso para acceder a la localización
        if ( (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) && (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ) {
            return;
        }

        //Accedemos al servicio de localización
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Obtenemos la lista de proveedores disponibles (activos)
        boolean soloActivos = true;
        List<String> proveedores = locationManager.getProviders(soloActivos);

        if (proveedores.isEmpty()) { // No hay ninguno activo y no se puede hacer nada
            return;
        }

        // Vemos si está disponible el proveedor de localización que queremos usar
        String provider = LocationManager.GPS_PROVIDER;
        boolean disponible = proveedores.contains(provider);

        // Otra opción es utilizar uno cualquiera de la lista (por ejemplo, el primero)
        if (!disponible) {
            provider = proveedores.get(0);
        }

        //Pedimos la última localización conocida por el proveedor
        Location location = locationManager.getLastKnownLocation(provider);

        // Actualizamos las coordenadas
        actualizarUbicacion(location);

        //Tiempo mínimo entre escuchas de nueva posición
        int t = 1000; //milisegundos
        //Distancia mínima entre escuchas de nueva posición
        int distance = 50; //metros

        //Pedimos escuchar actualizaciones de posicion que reciba el proveedor
        locationManager.requestLocationUpdates(provider, t, distance, locListener);

    }

    //Botón EMPEZAR RUTA
    public void clickStartRoute(View view) {

        // Obtenemos los objetos para escribir el nombre de la ruta
        final EditText putNameRoute = (EditText) findViewById(R.id.putNameRoute);
        final Button button_okroute = (Button) findViewById(R.id.button_okroute);

        // Hacemos visibles los objetos
        putNameRoute.setVisibility(View.VISIBLE);
        button_okroute.setVisibility(View.VISIBLE);

        // Una vez escrito el nombre pulsamos el botón
        button_okroute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Mostrar nombre de la ruta
                TextView routeName = (TextView) findViewById(R.id.routeName);
                route_name = putNameRoute.getText().toString();
                routeName.setText(route_name);

                // Guardar ruta en BD y esconder otra vez los objetos
                rowid = dbAdapter.createRoute(route_name);
                button_okroute.setVisibility(View.INVISIBLE);
                putNameRoute.setVisibility(View.INVISIBLE);
            }
        });
    }

    // Botón FINALIZAR RUTA
    public void clickEndRoute(View view) {

        // Se termina de guardar coordenadas en la ruta y se muestra la ruta ya creada en el ListView
        setResult(RESULT_OK);

        Intent intent = new Intent (Activity_newroute.this, Activity_savedroutes.class);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Nueva ruta creada!", Toast.LENGTH_LONG).show();

    }

    public void centerMap(double latitude, double longitude){

        // A partir de una pareja de coordenadas (tipo double) creamos un objeto LatLng,
        // que es el tipo de dato que debemos usar al tratar con mapas
        LatLng position = new LatLng(latitude, longitude);

        // Características de la cámara
        float zoom = 15;      // nivel de zoom
        float angulo = 80;    // ángulo equivalente de visión
        float bearing = 0;    // orientación de la cámara

        // Cambiar posición de la cámara
        CameraPosition camPos = new CameraPosition.Builder()
                .target(position)
                .zoom(zoom)
                .bearing(bearing)
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

    // -------------------------------------------


}
