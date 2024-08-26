package com.example.weather_app;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import kotlinx.coroutines.scheduling.Task;

public class MainActivity extends AppCompatActivity {

    TextView Cityname;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder results = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    results.append(line).append("\n");
                }
                reader.close();
                return results.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject main = jsonObject.getJSONObject("main");
                    String weatherInfo = main.getString("temp");
                    weatherInfo=weatherInfo.replace("temp" ,"Temperature");
                    weatherInfo=weatherInfo.replace("feels like" ,"Feels like");
                    weatherInfo=weatherInfo.replace("temp_max" ,"Temperature max");
                    weatherInfo=weatherInfo.replace("temp_min" ,"Temperature min");
                    weatherInfo=weatherInfo.replace("pressure" ,"Pressure");
                    show.setText("Temperature: " + weatherInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                    show.setText("Error parsing data");
                }
            } else {
                show.setText("Cannot find weather");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Cityname = findViewById(R.id.Cityname);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        final String[] temp={""};

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_SHORT).show();
                String city = Cityname.getText().toString().trim();
                try{
                    if (city!=null) {
                        url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=cf846fc128e8484396f33ad227644ac6";
                    }else {
                        Toast.makeText(MainActivity.this, "Enter city", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task=new getWeather();
                    temp[0]= task.execute(url).get();
                }catch(ExecutionException e ){
                    e.printStackTrace();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp[0]==null){
                    show.setText("cannot find");
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}


