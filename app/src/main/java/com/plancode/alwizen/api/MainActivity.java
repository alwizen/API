package com.plancode.alwizen.api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private ListView listView;

    //URL API
    private static String url = "http://mutiara.nyimak.id/api/quotes";

    ArrayList<HashMap<String, String>> quoteList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteList = new ArrayList<>();
        listView = (ListView)findViewById(R.id.list);

        new GetQuotes().execute();
    }

    private class GetQuotes extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            //progres Dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Mohon Tunggu. . .");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
            //Membuat req url
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Respon dari url" +jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    JSONArray quotes=jsonObject.getJSONArray("qoutes");

            //perulangan quote

            for (int i = 0; i < quotes.length(); i++) {
                JSONObject c = quotes.getJSONObject(i);

                String id = c.getString("id_quotes");
                String judul = c.getString("judul_quotes");
                String namaCategory = c.getString("nama_category");
                String namaUser = c.getString("nama_user");
                HashMap<String, String> kataKata = new HashMap<>();
                     kataKata.put("id", id);
                     kataKata.put("Judul", judul);
                     kataKata.put("Nama Kategori", namaCategory);
                     kataKata.put("Nama User", namaUser);


                // menambah uote
                quoteList.add(kataKata);
            }  } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }} else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Tidak bisa terhubung API",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating JSON data ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, quoteList,
                    R.layout.list_item, new String[]{"id", "judul",
                    "Nama Kategori", "Nama User"}, new int[]{R.id.id,
                    R.id.judul, R.id.nmKategori, R.id.user});

            listView.setAdapter(adapter);
        }

    }
}
