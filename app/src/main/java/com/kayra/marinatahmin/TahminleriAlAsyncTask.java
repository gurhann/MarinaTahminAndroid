package com.kayra.marinatahmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kayra.marinatahmin.model.Liman;
import com.kayra.marinatahmin.model.Tahmin;
import com.kayra.marinatahmin.model.Tarih;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurhan on 15.03.2015.
 */
public class TahminleriAlAsyncTask extends AsyncTask<Void, String, List<Tarih>> {

    private Context context;
    private Liman liman;
    ProgressDialog progressDialog;

    public TahminleriAlAsyncTask(Context context, Liman liman) {
        super();
        this.context = context;
        this.liman = liman;

    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Lütfen Bekleyin...", "İşlem Yürütülüyor", true);
    }


    @Override
    protected List<Tarih> doInBackground(Void... params) {
        List<Tarih> gunTahminleri = new ArrayList<>();
        publishProgress("Http Bağlantısı Kuruluyor...");
        try {

            HttpClient client = new DefaultHttpClient();
            Gson gson = new Gson();
            String url = "http://192.168.1.112:8080/MarinaTahminWeb/RestService/Marina/tahminAl/" + liman.getId();

            HttpGet get = new HttpGet(url);
            get.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();

            String returnedJson = getJsonFromResponse(entity.getContent());
            Tarih[] tahminlerArray = gson.fromJson(returnedJson, Tarih[].class);
            for (Tarih t : tahminlerArray) {
                gunTahminleri.add(t);
            }

            return gunTahminleri;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setMessage(values[0]);
    }

    @Override
    protected void onPostExecute(List<Tarih> gunTahminleri) {
        String s = listele(gunTahminleri);
        TextView t = (TextView) ((Activity) context).findViewById(R.id.sonucTextView);
        t.setText(s);
        progressDialog.cancel();
    }

    public String getJsonFromResponse(InputStream is) throws IOException {
        String json = "";
        String line;
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((line = rd.readLine()) != null) {
            json += line;
        }
        rd.close();
        return json;
    }

    
    private String listele(List<Tarih> gunler) {
        String s = "";
        publishProgress("Liste güncelleniyor");
        try {


            for (Tarih t : gunler) {
                s += t.getTarih() + "\n";
                for (Tahmin tt : t.getGunTahminleri()) {
                    s += tt.getSaat() + "\n" + tt.getHavaDurumu()
                            + " : " + tt.getSicaklik() + " : "
                            + tt.getRuzgarYonu() + " : "
                            + tt.getRuzgarHizi() + "\n"
                            + tt.getDalgaRuzgarYonu() + " : "
                            + tt.getDalgaRuzgarHizi() + " \n "
                            + tt.getDalgaYonu() + " : "
                            + tt.getDalgaYuksekligi() + "\n";
                }
            }
        } catch (NullPointerException e) {
            s = "liste boş geldi";
        }
        return s;
    }
}
