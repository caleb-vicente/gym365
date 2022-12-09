package com.gym365.it.gym;

//import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.Arrays;
import java.util.List;

public class Activity_recomroutesmap extends FragmentActivity implements OnMapReadyCallback {

    // Nuestro mapa
    GoogleMap mMap;

    // variable para seleccionar la ruta
    private static int ruta = 0;

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
        setContentView(R.layout.activity_routesmap);

        // Bundle para obtener datos de la anterior clase
        Bundle select_ruta = getIntent().getExtras();

        if (select_ruta.getInt("RUTA")==1)
            ruta = 1;
        if (select_ruta.getInt("RUTA")==2)
            ruta = 2;
        if (select_ruta.getInt("RUTA")==3)
            ruta = 3;

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

        // Dadas unas coordenadas, centramos el mapa en ellas
        double latitude = 40.3320;
        double longitude = -3.7687;

        // dibujar ruta correspondiente
        if (ruta==1) {
            mostrarRuta1();
            centerMap(latitude, longitude, 120); // SUR-ESTE
        }
        else if (ruta==2) {
            mostrarRuta2();
            centerMap(latitude, longitude, 270); // OESTE
        }
        else if (ruta==3) {
            mostrarRuta3();
            centerMap(latitude, longitude, 0); // NORTE
        }

    }

    // Leganés - Getafe
    private void mostrarRuta1()
    {
        //Dibujo con Lineas
        PolylineOptions lineas = new PolylineOptions()
                .add(new LatLng(40.3320, -3.7687))
                .add(new LatLng(40.33219857920399, -3.768797994223974))
                .add(new LatLng(40.32940437063031, -3.762818131298213))
                .add(new LatLng(40.32814575859535, -3.758582135834296))
                .add(new LatLng(40.32541883396253, -3.76090275634554))
                .add(new LatLng(40.32157905100918, -3.752684671597567))
                .add(new LatLng(40.31869729038561, -3.746518372453772))
                .add(new LatLng(40.31687350189041, -3.744854331704378))
                .add(new LatLng(40.31495393498454, -3.742471052012011))
                .add(new LatLng(40.31323442816871, -3.740216630907724))
                .add(new LatLng(40.31166219434951, -3.738088167964317))
                .add(new LatLng(40.31048853704499, -3.736348501070706))
                .add(new LatLng(40.30923007019094, -3.734950446950465))
                .add(new LatLng(40.30760898639011, -3.734785930060124))
                .add(new LatLng(40.30888501147118, -3.734103343466816))
                .add(new LatLng(40.30828449963889, -3.732452857908771));
        lineas.width(11);
        lineas.color(Color.BLUE);
        lineas.pattern(PATTERN_DASHED); // rayado
        mMap.addPolyline(lineas);
    }

    // Leganés - Alcorcón
    private void mostrarRuta2()
    {
        //Dibujo con Lineas
        PolylineOptions lineas2 = new PolylineOptions()
                 .add(new LatLng(40.3320, -3.7687))
                 .add(new LatLng(40.33212927051316, -3.768731381868963))
                 .add(new LatLng(40.33113879238059, -3.770150752948922))
                 .add(new LatLng(40.3331222538031, -3.773904164076237))
                 .add(new LatLng(40.33392794691954, -3.776171378877916))
                 .add(new LatLng(40.3346655795203, -3.78040918857087))
                 .add(new LatLng(40.33603084234792, -3.783660610554692))
                 .add(new LatLng(40.33608947607797, -3.788616000358098))
                 .add(new LatLng(40.33804607673859, -3.789923494269808))
                 .add(new LatLng(40.34003990907549, -3.796467037276121))
                 .add(new LatLng(40.34166437403189, -3.800308711039936))
                 .add(new LatLng(40.3414397044932,  -3.801842681435542))
                 .add(new LatLng(40.34360624303641, -3.807743292080335))
                 .add(new LatLng(40.34528342891914, -3.811860479028907))
                 .add(new LatLng(40.34369716883603, -3.813646247059236))
                 .add(new LatLng(40.3414481700831, -3.81622763173609))
                 .add(new LatLng(40.34211085180559, -3.82007734775123))
                 .add(new LatLng(40.34334613338678, -3.822277453690911))
                 .add(new LatLng(40.34502836857398, -3.823730384740943))
                 .add(new LatLng(40.34541155596254, -3.826896354513748))
                 .add(new LatLng(40.34706616802492, -3.827816838318467));
        lineas2.width(14);
        lineas2.color(Color.RED);
        lineas2.pattern(PATTERN_DOTTED); // puntitos
        mMap.addPolyline(lineas2);
    }

    // Leganés - Casa de Campo
    private void mostrarRuta3()
    {
        //Dibujo con Lineas
        PolylineOptions lineas3 = new PolylineOptions()
                .add(new LatLng(40.3320, -3.7687))
                .add(new LatLng(40.33213703783161, -3.768831824685557))
                .add(new LatLng(40.33488196400786, -3.763723991115881))
                .add(new LatLng(40.33820783331308, -3.759091638690152))
                .add(new LatLng(40.34172491272173, -3.756652288043205))
                .add(new LatLng(40.34693880017777, -3.755675311596133))
                .add(new LatLng(40.35792114014436, -3.74736709315501))
                .add(new LatLng(40.36183498992182, -3.745412928717164))
                .add(new LatLng(40.36798563216385, -3.74199741543776))
                .add(new LatLng(40.37337190214336, -3.734639436710273))
                .add(new LatLng(40.37449443105885, -3.738565272035058))
                .add(new LatLng(40.37282249800113, -3.743215835313332))
                .add(new LatLng(40.37747652547525, -3.755429920763539))
                .add(new LatLng(40.38082487206464, -3.756650543800695))
                .add(new LatLng(40.3849162737547, -3.761289802621679))
                .add(new LatLng(40.38491755038741, -3.752743600985337))
                .add(new LatLng(40.38585302099968, -3.750299494504123))
                .add(new LatLng(40.38808419316441, -3.752010572335997))
                .add(new LatLng(40.3932869330164, -3.752010942977674))
                .add(new LatLng(40.39514642870447, -3.758116444125441))
                .add(new LatLng(40.40875647603812, -3.760813284708551))
                .add(new LatLng(40.41212177082335, -3.754211491203573))
                .add(new LatLng(40.41530948239769, -3.748826776124217));
        lineas3.width(11);
        lineas3.color(Color.BLACK);
        lineas3.pattern(PATTERN_MIXED); // mixto
        mMap.addPolyline(lineas3);

    }


    public void centerMap(double latitude, double longitude, float bearing){

        // A partir de una pareja de coordenadas (tipo double) creamos un objeto LatLng,
        // que es el tipo de dato que debemos usar al tratar con mapas
        LatLng position = new LatLng(latitude, longitude);

        float zoom = 14;
        float angulo = 80;

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

}