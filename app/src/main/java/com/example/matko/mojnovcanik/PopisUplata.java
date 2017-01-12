package com.example.matko.mojnovcanik;


import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;



public class PopisUplata extends ActionBarActivity {

    //potrebno kako bi swipanje bilo moguće
    private GestureDetectorCompat gestureDetectorCompat;

    InputStream is = null;
    String result = null;
    String line = null;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popis_uplata);

        //također za swipanje
        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());


        //za string korisnika da provlači kroz sve Activitije
        Intent intent = getIntent();
        String korisnik = intent.getExtras().getString("korisnikDisplay");


        //pokreće se metoda PrikaziUplate koja se spaja na skriptu koja dohvaća sve uplate te ih zatim metoda sprema u ListVIew i prikazuje u aplikaciji
        PrikaziUplate();


    }




//METODA ZA ISPISIVANJE UPLATA

public void PrikaziUplate(){




        Intent intent = getIntent();
        String korisnik = intent.getExtras().getString("korisnikDisplay");

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("ime", korisnik));

        try {
            //Spajanje na server gdje se nalazi skripta koja ispisuje sve uplate
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://matkohorvat.cloudapp.net/Android-SpajanjeBaze/prikazUplata.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "konekcija uspješna ");
        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Pogrešna IP Adresa",
                    Toast.LENGTH_LONG).show();
        }

        try {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "konekcija uspješna ");
        } catch (Exception e) {
            Log.e("Fail 2", e.toString());
        }

        try {

            List<String> Uplata = new ArrayList<String>();




            JSONArray Jarray = new JSONArray(result);
            //Pomoću for petlje se prolazi kroz sve uplate
            for (int i = 0; i < Jarray.length(); i++) {


                JSONObject Jasonobject = null;

                Jasonobject = Jarray.getJSONObject(i);

                String ime = Jasonobject.getString("korisnik");
                String naziv = Jasonobject.getString("nazivUplate");
                String iznos = Jasonobject.getString("iznos");

                //Ako je trenutno prijavljeni korisnik isti kao onaj koji je napravio tu uplatu u tablici
                // tada se ona pomoću adaptera sprema u
                // ListView i tako prikazuje u aplikaciji
                if(korisnik.equals(ime)) {
                    Uplata.add(naziv+"  <----->  "+iznos+" kn");


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (this,
                                    R.layout.layout_popis_uplata_stavka,
                                    R.id.podaciUplate,
                                    Uplata);



                    ListView list = (ListView) findViewById(R.id.listaUplata);

                    list.setAdapter(adapter);




                }



            }






        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }





    }



//METODA ZA ISPISIVANJE STANJA ---KRAJ








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popis_uplata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //Poziva se obrazac za uplatu iz menija
        if (id == R.id.uplata) {

            Intent intent = getIntent();
            String korisnik = intent.getExtras().getString("korisnikDisplay");

            Intent i = new Intent(PopisUplata.this,
                    Uplata.class);
            i.putExtra("korisnikDisplay",korisnik);
            finish();
            startActivity(i);

        }

        //poziva se obrazac za isplatu iz menija
        else if(id == R.id.isplata){

            Intent intent = getIntent();
            String korisnik = intent.getExtras().getString("korisnikDisplay");

            Intent i= new Intent(PopisUplata.this, Isplata.class);
            i.putExtra("korisnikDisplay",korisnik);
            finish();
            startActivity(i);
        }


        return super.onOptionsItemSelected(item);
    }


    //za swipanje POČETAK

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            Intent intent = getIntent();
            String korisnik = intent.getExtras().getString("korisnikDisplay");


            //ako se swipea u LIJEVO
            if(event2.getX() < event1.getX()){



                //prebacivanje na ISPLATE
                Intent i = new Intent(PopisUplata.this,
                        PopisIsplata.class);
                i.putExtra("korisnikDisplay",korisnik);
                finish();
                startActivity(i);
            }
            //ako se swipea u DESNO
            else if(event2.getX() > event1.getX()){
                //prebacivanje na IZNOS
                Intent i = new Intent(PopisUplata.this, Iznos.class);
                i.putExtra("korisnikDisplay",korisnik);
                finish();
                startActivity(i);
            }

            return true;
        }
    }
}

//KRAJ za swipanje

