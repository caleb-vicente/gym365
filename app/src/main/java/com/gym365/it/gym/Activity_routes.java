package com.gym365.it.gym;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Activity_routes extends AppCompatActivity {

    // indicar API KEY para el API de tipo "browser" de Google Places
    final String AEMET_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYWxlYnZpY2VudGU5OEBnbWFpbC5jb20iLCJqdGkiOiJhMWRmYjZmYy0yNDQ5LTRjOWQtYWFmMy0wOTU2MjIxNTc1NjEiLCJpc3MiOiJBRU1FVCIsImlhdCI6MTU4OTAxMzM0NywidXNlcklkIjoiYTFkZmI2ZmMtMjQ0OS00YzlkLWFhZjMtMDk1NjIyMTU3NTYxIiwicm9sZSI6IiJ9.SrEAN6zxNy6oBYbIG4O_VWmEc011a06-9_sBZBJ__GQ";

    private ListView m_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inflamos el layout
        setContentView(R.layout.activity_routes);

        // Creamos un listview que va a contener los resultados de las consulta a Google Places
        m_listview = (ListView) findViewById(R.id.id_list_view);

        new Activity_routes.AemetFirstRequest().execute();
    }

    private class AemetFirstRequest extends AsyncTask<View, Void, String> {

        @Override
        protected String doInBackground(View... urls) {
            String first_url;
            // make Call to the url
            //first_url = makeCallFirstRequest("https://opendata.aemet.es/opendata/api/valores/climatologicos/diarios/datos/fechaini/2017-01-01T00%3A00%3A00UTC/fechafin/2017-01-02T00%3A00%3A00UTC/estacion/2462?api_key="+AEMET_KEY);
            first_url = makeCallFirstRequest("https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/diaria/28074?api_key="+AEMET_KEY);

            return first_url;
        }

        @Override
        protected void onPreExecute() {
            // we can start a progress bar here
        }

        @Override
        protected void onPostExecute(String result) {
            new Activity_routes.AemetSecondRequest().execute(result);
        }
    }
    private class AemetSecondRequest extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(String... result) {
            ArrayList<ArrayList<String>> temp;
            // make Call to the url
            Log.w("TAG", "eL NUEVO CAMPOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO ES: "+result[0]);
            temp = makeCallSecondRequest(result[0]);

            return temp;
        }

        @Override
        protected void onPreExecute() {
            // we can start a progress bar here
        }
        public String selectWeatherIcon(String stateSki) {
            String icon = "";
            switch(stateSki) {
                case "Muy nuboso":
                    icon = "icono_muy_nuboso";
                    break;
                case "Muy nuboso con lluvia escasa":
                case "Muy nuboso con lluvia":
                    icon="icono_muy_nuboso_con_lluvia";
                    break;
                case "Cubierto con lluvia":
                    icon = "icono_cubierto_con_lluvia";
                    break;
                case "Cubierto":
                    icon = "icono_cubierto";
                    break;
                case "Intervalo nuboso":
                case "Poco nuboso":
                    icon = "icono_intervalo_nuboso";
                    break;
                case "Despejado":
                    icon = "icono_sol";
                    break;

                default:
                    icon = "icono_intervalo_nuboso";
            }
            return icon;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> temp) {
            // Aquí se actualiza el interfaz de usuario
            List<String> listTitle = new ArrayList<String>();
            List<String> max_min = temp.get(0);
            List<String> description = temp.get(2);
            Log.w("TAG", "maaaaaaaaaaaaxmiiiiiiiiiiiiiiiiiinrecogido"+max_min);

            TextView max = (TextView) findViewById(R.id.temp_max);
            TextView min = (TextView) findViewById(R.id.temp_min);
            ImageView description_image = (ImageView) findViewById(R.id.temp_icono);

            max.setText(max_min.get(0)+"º");
            min.setText(max_min.get(1)+"º");

            //función que elige un icono dependiendo del tipo de dia
            String icon="";
            icon = "@drawable/"+selectWeatherIcon(description.get(2));
            int imageResource = getResources().getIdentifier(icon, null, getPackageName());
            description_image.setImageResource(imageResource);

        }
    }

    public static String makeCallFirstRequest(String stringURL) {
        //Este método hace la primera consulta a aemet, que devuelve un json, del cual se obtiene una url, que contendrá la propia consulta
        URL url = null;
        BufferedInputStream is = null;
        JsonReader jsonReader;
        String datos = "";

        try {
            url = new URL(stringURL);
        } catch (Exception ex) {
            System.out.println("Malformed URL");
        }

        try {
            if (url != null) {
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                is = new BufferedInputStream(urlConnection.getInputStream());
            }
        } catch (IOException ioe) {
            System.out.println("IOException");
        }

        if (is != null) {
            try {
                jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    // Busca la cadena "results"
                    if (name.equals("datos")) {
                        datos = jsonReader.nextString();
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            } catch (Exception e) {
                System.out.println("Exception");
                return "fallo";
            }
        }

        return datos;}


    public static ArrayList<ArrayList<String>> makeCallSecondRequest(String stringURL) {
        //Este método hace la consulta a la
        URL url = null;
        BufferedInputStream is = null;
        JsonReader jsonReader;
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        ArrayList<String> max_min = new ArrayList<String>();
        ArrayList<String> temperature = new ArrayList<String>();
        ArrayList<String> description = new ArrayList<String>();
        String datos;

        try {
            url = new URL(stringURL);
        } catch (Exception ex) {
            System.out.println("Malformed URL");
        }

        try {
            if (url != null) {
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                is = new BufferedInputStream(urlConnection.getInputStream());
            }
        } catch (IOException ioe) {
            System.out.println("IOException");
        }

        if (is != null) {
            try {

                jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));

                jsonReader.beginArray();

                while (jsonReader.hasNext()) {
                    jsonReader.beginObject();

                    while( jsonReader.hasNext() ) {

                        String name = jsonReader.nextName();
                        // Busca la cadena "results"
                        if (name.equals("prediccion")) {
                            jsonReader.beginObject();

                            while (jsonReader.hasNext()) {
                                name = jsonReader.nextName();
                                if (name.equals("dia")) {

                                    jsonReader.beginArray();
                                    while (jsonReader.hasNext()) {
                                        jsonReader.beginObject();
                                        // comienza un objeto
                                        while (jsonReader.hasNext()) {
                                            name = jsonReader.nextName();
                                            if (name.equals("temperatura")) {
                                                jsonReader.beginObject();
                                                while (jsonReader.hasNext()) {
                                                    name = jsonReader.nextName();
                                                    if (name.equals("maxima")) {
                                                        datos = jsonReader.nextString();
                                                        max_min.add(datos);
                                                    } else if (name.equals("minima")) {
                                                        datos = jsonReader.nextString();
                                                        max_min.add(datos);
                                                    } else if (name.equals("dato")) {
                                                        jsonReader.beginArray();
                                                        while (jsonReader.hasNext()) {
                                                            jsonReader.beginObject();
                                                            // comienza un objeto
                                                            while (jsonReader.hasNext()) {
                                                                name = jsonReader.nextName();
                                                                if (name.equals("value")) {
                                                                    datos = jsonReader.nextString();
                                                                    temperature.add(datos);
                                                                } else if (name.equals("hora")) {
                                                                    datos = jsonReader.nextString();
                                                                    temperature.add(datos);
                                                                } else {
                                                                    jsonReader.skipValue();
                                                                }

                                                            }
                                                            jsonReader.endObject();
                                                        }
                                                        jsonReader.endArray();
                                                    } else {
                                                        jsonReader.skipValue();
                                                    }
                                                }
                                                jsonReader.endObject();
                                            }else if (name.equals("estadoCielo")){
                                                jsonReader.beginArray();
                                                while (jsonReader.hasNext()) {
                                                    jsonReader.beginObject();
                                                    // comienza un objeto
                                                    while (jsonReader.hasNext()) {
                                                        name = jsonReader.nextName();
                                                        if (name.equals("descripcion")) {
                                                            datos = jsonReader.nextString();
                                                            description.add(datos);
                                                        }else {
                                                            jsonReader.skipValue();
                                                        }
                                                    }
                                                    jsonReader.endObject();
                                                }
                                                jsonReader.endArray();
                                            } else {
                                                jsonReader.skipValue();
                                            }
                                        }
                                        jsonReader.endObject();


                                    }
                                    jsonReader.endArray();

                                } else {
                                    jsonReader.skipValue();
                                }
                            }
                            jsonReader.endObject();
                        } else {
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                }
                jsonReader.endArray();

            } catch (Exception e) {
                System.out.println("Exception");
                //return new ArrayList<String>();
            }
        }

        temp.add(max_min);
        temp.add(temperature);
        temp.add(description);
        return temp;}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Pulsación de los botones

    public void clickNuevaRuta(View view) {

        Intent intent = new Intent (this, Activity_newroute.class);
        startActivity(intent);

    }

    public void clickRutasGuardadas(View view) {

        Intent intent = new Intent (this, Activity_savedroutes.class);
        startActivity(intent);

    }

    public void clickRutasRecomendadas(View view) {

        Intent intent = new Intent (this, Activity_recomroutes.class);
        startActivity(intent);

    }

}