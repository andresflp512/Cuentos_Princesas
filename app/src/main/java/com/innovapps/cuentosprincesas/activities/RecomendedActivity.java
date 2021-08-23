package com.innovapps.cuentosprincesas.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.innovapps.cuentosprincesas.R;
import com.innovapps.cuentosprincesas.adapters.RecomendedAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecomendedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomended);
        app = getString(R.string.package_name);

        this.setTitle(R.string.recomended);

        myrv = findViewById(R.id.recyclerview_id);
        hilos();
    }
    RecyclerView myrv;

    ObtenerWebService hiloconexion;

    //Variables -------------

    String IP = "http://full-apps-org.com/Devs/api/";

    String GET = IP + "query_publicidad.php?texto=";

    String id = "13";

    String app;

    //Array Series

    public static ArrayList<String> array_series_id = new ArrayList<String>();

    public static ArrayList<String> array_series_titulo = new ArrayList<String>();

    public static ArrayList<String> array_series_foto = new ArrayList<String>();

    public static ArrayList<String> array_series_precio = new ArrayList<String>();

    public static ArrayList<String> array_series_cantidad = new ArrayList<String>();

    public static ArrayList<String> array_series_descripcion= new ArrayList<String>();

    //----------

    RecomendedAdapter myAdapterC;

    private void hilos(){
        array_series_cantidad.clear();array_series_descripcion.clear();array_series_foto.clear();array_series_precio.clear();array_series_titulo.clear();
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(GET + id + "&app=" + getString(R.string.package_name),"1");

    }

    public class ObtenerWebService extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];

            URL url = null; // url de donde queremos obtener la informacion

            String devuelve = "";

            if(params[1]=="1"){  //consulta por id

                try {
                    url = new URL(cadena);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //abrir la coneccion
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                            "(Linux; Android 1.5; es-ES) Ejemplo HTTP");

                    int respuesta = connection.getResponseCode();
                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK){

                        InputStream in = new BufferedInputStream(connection.getInputStream());  //Preparo la cadena de entrada

                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));  //Le introdusco en un BufferReader

                        String line;
                        while ((line = reader.readLine()) != null){
                            result.append(line); //pasa toda la entrada al StringBuilder
                        }

                        //creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto
                        JSONObject respuestaJSON = new JSONObject(result.toString()); //Creo un JSONObject apartir de un JSONObject

                        String resultJSON = respuestaJSON.getString("estado");//Estado es el nombre del campo en el JSON

                        Log.d(resultJSON,"esto es lo q trajo el estado");

                        if (resultJSON.equals("1")){ //hay alumnos a mostrar

                            JSONArray alumnosJSON = respuestaJSON.getJSONArray("oficinas"); //estado es el nombre del campo en el JSON

                            Log.d(alumnosJSON.getJSONObject(0).getString("img"),"esto es lo q trajo el estado f4era de cumple");

                            //mensaje(alumnosJSON.getJSONObject(0).getString("grupo"));

                            for (int i = 0; i < alumnosJSON.length(); i++) {

                                array_series_cantidad.add("0");
                                array_series_descripcion.add("0");
                                array_series_foto.add(alumnosJSON.getJSONObject(i).getString("img"));
                                array_series_precio.add("0");
                                array_series_titulo.add(alumnosJSON.getJSONObject(i).getString("link"));

                                Log.d("PRUEBA", alumnosJSON.getJSONObject(i).getString("img"));

                            }

                        }
                        else if (resultJSON.equals("2")){

                            devuelve = "No hay cumpleaños este dia";
                        }

                    }

                }catch (MalformedURLException e){
                    devuelve = e.toString();
                }catch (IOException e) {
                    devuelve = e.toString();
                } catch (JSONException e) {
                    devuelve = e.toString();
                }

                return devuelve;

            }

            return null;
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPostExecute(String s) {

            myAdapterC = new RecomendedAdapter(RecomendedActivity.this, array_series_foto, array_series_titulo,array_series_id, array_series_precio, array_series_descripcion, array_series_cantidad);
            myrv.setLayoutManager(new GridLayoutManager(RecomendedActivity.this,1));
            myrv.setAdapter(myAdapterC);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}