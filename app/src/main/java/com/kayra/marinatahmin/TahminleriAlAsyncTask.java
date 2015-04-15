package com.kayra.marinatahmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
    private ProgressDialog progressDialog;
    private TableLayout marinaTahminTableLayout;
    private TableLayout dalgaTahminTableLayout;
    private TextView tarihTextView;
    private ImageButton oncekiImageButton;
    private ImageButton sonrakiImageButton;
    private TextView sayfaTextView;
    private List<Tarih> gunTahminleri;
    public TahminleriAlAsyncTask(Context context, Liman liman) {
        super();
        this.context = context;
        this.liman = liman;
        marinaTahminTableLayout = (TableLayout)((Activity) context).findViewById(R.id.marinaTahminTableLayout);
        dalgaTahminTableLayout = (TableLayout)((Activity) context).findViewById(R.id.dalgaTahminTableLayout);
        tarihTextView = (TextView)((Activity) context).findViewById(R.id.tarihTextView);
        oncekiImageButton = (ImageButton)((Activity) context) .findViewById(R.id.oncekiSayfaImageButton);
        sonrakiImageButton = (ImageButton)((Activity) context) .findViewById(R.id.sonrakiSayfaImageButton);
        sayfaTextView = (TextView)((Activity) context).findViewById(R.id.sayfaTextView);
        oncekiSonrakiAyarla();

    }

    private void oncekiSonrakiAyarla() {
        sayfaTextView.setText("1");
        aktifOlmaliMi();
        oncekiImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int sayfa = Integer.parseInt(sayfaTextView.getText().toString());
                sayfaTextView.setText(String.valueOf(sayfa-1));
                onPostExecute(gunTahminleri);
                aktifOlmaliMi();
            }
        });

        sonrakiImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int sayfa = Integer.parseInt(sayfaTextView.getText().toString());
                sayfaTextView.setText(String.valueOf(sayfa+1));
                onPostExecute(gunTahminleri);
                aktifOlmaliMi();
            }
        });
    }
    private void aktifOlmaliMi() {
        if(sayfaTextView.getText().toString().equals("1")){
            oncekiImageButton.setEnabled(false);
            oncekiImageButton.setVisibility(View.INVISIBLE);
        }else {
            oncekiImageButton.setEnabled(true);
            oncekiImageButton.setVisibility(View.VISIBLE);
        }
        if(sayfaTextView.getText().toString().equals("3")){
            sonrakiImageButton.setEnabled(false);
            sonrakiImageButton.setVisibility(View.INVISIBLE);
        }else {
            sonrakiImageButton.setEnabled(true);
            sonrakiImageButton.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Lütfen Bekleyin...", "İşlem Yürütülüyor", true);
    }


    @Override
    protected List<Tarih> doInBackground(Void... params) {
        gunTahminleri = new ArrayList<>();
        publishProgress("Http Bağlantısı Kuruluyor...");
        try {

            HttpClient client = new DefaultHttpClient();
            Gson gson = new Gson();
            String url = "http://gkucuk.koding.io:8080/MarinaTahminWeb-1.0/RestService/Marina/tahminAl/" + liman.getId();

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
    protected void onPostExecute(List<Tarih> ucGunlukTahmin) {


        marinaTahminTableLayout.removeAllViews();
        dalgaTahminTableLayout.removeAllViews();

        int sayfa = Integer.parseInt(sayfaTextView.getText().toString());
        Tarih tt = ucGunlukTahmin.get(sayfa - 1);
        tarihTextView.setText(tt.getTarih());
        String saatler [] = new String[ucGunlukTahmin.get(sayfa-1).getKolonSayisi()];

        //ilk satıda Bilgi / saat   saatler şeklinde başlık atılır.
        for(int i = 0; i < saatler.length;i++) {
            saatler[i] = tt.getGunTahminleri().get(i).getSaat();
        }
        TableRow baslikSatir = new TableRow(context);

        TextView bilgi = new TextView(context);
        bilgi.setText("Bilgi/Saat");
        bilgi.setWidth(130);

        baslikSatir.addView(bilgi);

        for(String ss : saatler) {
            TextView tv = new TextView(context);
            tv.setWidth(50);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(ss);
            baslikSatir.addView(tv);
        }
        marinaTahminTableLayout.addView(baslikSatir);
        
        // Marina Tahminleri
        List<Tahmin> gunTahmin = ucGunlukTahmin.get(sayfa-1).getGunTahminleri();
        bilgileriEkle(gunTahmin, "Sıcaklık");
        bilgileriEkle(gunTahmin, "Hava Durumu");
        bilgileriEkle(gunTahmin, "Rüzgar Yönü");
        bilgileriEkle(gunTahmin, "Rüzgar Hızı");
        // Dalga Tahminleri
        bilgileriEkle(gunTahmin, "DT-Rüzgar Yönü");
        bilgileriEkle(gunTahmin, "DT-Rüzgar Hızı");
        bilgileriEkle(gunTahmin, "DT-Dalga Yönü");
        bilgileriEkle(gunTahmin, "DT-Dalga Yüksekliği");
        progressDialog.cancel();
    }

    public void sicakliklariEkle(List<Tahmin> gunTahminleri) {

        TableRow row = new TableRow(context);
        TextView bilgiTextView = new TextView(context);
        bilgiTextView.setText("Sıcaklık");
        bilgiTextView.setWidth(130);
        row.addView(bilgiTextView);



    }

    public void bilgileriEkle(List<Tahmin> gunTahminleri,String bilgi) {
        TableRow row = new TableRow(context);
        TextView bilgiTextView = new TextView(context);
        if(bilgi.contains("DT-")) {
            bilgiTextView.setText(bilgi.replace("DT-", ""));
        }else {
            bilgiTextView.setText(bilgi);
        }
        bilgiTextView.setWidth(120);
        bilgiTextView.setHeight(50);
        bilgiTextView.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(bilgiTextView);

        switch (bilgi) {
            case "Sıcaklık":
                for(Tahmin tahmin : gunTahminleri) {
                    Double sicaklik = tahmin.getSicaklik();
                    TextView tv = new TextView(context);
                    tv.setWidth(50);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setText(String.valueOf(sicaklik));
                    row.addView(tv);

                }
                break;

            case "Hava Durumu":
                for (final Tahmin tahmin : gunTahminleri) {
                    String havaDurumu = tahmin.getHavaDurumu();
                    ImageView iv = new ImageView(context);
                    iv.setMaxWidth(50);
                    iv.setMinimumWidth(50);
                    iv.setMaxHeight(50);
                    iv.setMinimumHeight(50);

                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast toast = Toast.makeText(context, "Saat: " + tahmin.getSaat() +
                                    ", Hava Durumu: " + tahmin.getHavaDurumu(), Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

                    switch (havaDurumu) {
                        case "Açık":
                            iv.setImageResource(R.drawable.acik);
                            break;
                        case "Kapalı":
                            iv.setImageResource(R.drawable.kapali);
                            break;
                        case "Parçalı Çok Bulutlu":
                            iv.setImageResource(R.drawable.parcali_cok_bulutlu);
                            break;
                        case "Yağışlı":
                            iv.setImageResource(R.drawable.yagisli);
                            break;
                        case "Sağanak Yağışlı":
                            iv.setImageResource(R.drawable.saganak_yagisli);
                            break;
                        case "Karlı":
                            iv.setImageResource(R.drawable.karli);
                            break;
                        case "Karlı Yağmurlu":

                            iv.setImageResource(R.drawable.karli_yagmurlu);
                            break;
                        default:
                            iv.setImageResource(R.drawable.ic_launcher);
                            break;

                    }
                    row.addView(iv);
                }
                break;
            case "Rüzgar Yönü":
                for(final Tahmin tahmin : gunTahminleri) {
                    String ruzgarYonu = tahmin.getRuzgarYonu();
                    String kisaRuzgarYon;
                    if(!ruzgarYonu.equals("Sakin")) {
                        kisaRuzgarYon  = kisaHalAl(ruzgarYonu);
                    }else {
                        kisaRuzgarYon = "Sakin";
                    }
                    ImageView iv = new ImageView(context);
                    iv.setMaxWidth(50);
                    iv.setMinimumWidth(50);
                    iv.setMaxHeight(50);
                    iv.setMinimumHeight(50);

                    iv.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Saat: " + tahmin.getSaat() +
                                    ", Rüzgar Yönü: " + tahmin.getRuzgarYonu(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    switch (kisaRuzgarYon) {
                        case "Sakin":
                            iv.setImageResource(R.drawable.sakin);
                            break;
                        case "N":
                            iv.setImageResource(R.drawable.n);
                            break;
                        case "W":
                            iv.setImageResource(R.drawable.w);
                            break;
                        case "E":
                            iv.setImageResource(R.drawable.e);
                            break;
                        case "S":
                            iv.setImageResource(R.drawable.s);
                            break;
                        case "NE":
                            iv.setImageResource(R.drawable.ne);
                            break;
                        case "NW":
                            iv.setImageResource(R.drawable.nw);
                            break;
                        case "SE":
                            iv.setImageResource(R.drawable.se);
                            break;
                        case "SW":
                            iv.setImageResource(R.drawable.sw);
                            break;
                        default:
                            iv.setImageResource(R.drawable.ic_launcher);
                            break;
                    }
                    row.addView(iv);
                }
                break;
            case "Rüzgar Hızı":
                for (final Tahmin tahmin : gunTahminleri) {
                    String ruzgarHizi = tahmin.getRuzgarHizi();
                    TextView tv = new TextView(context);
                    tv.setWidth(50);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setText(kisaHalAl(ruzgarHizi));
                    tv.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Saat: " + tahmin.getSaat() +
                            "Rüzgar Hızı:" + tahmin.getRuzgarHizi(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    row.addView(tv);
                }
                break;
            case "DT-Rüzgar Yönü":
                for(final Tahmin tahmin : gunTahminleri) {
                    String ruzgarYonu = tahmin.getDalgaRuzgarYonu();
                    String kisaRuzgarYon;
                    if(!ruzgarYonu.equals("Sakin")) {
                        kisaRuzgarYon  = kisaHalAl(ruzgarYonu);
                    }else {
                        kisaRuzgarYon = "Sakin";
                    }
                    ImageView iv = new ImageView(context);
                    iv.setMaxWidth(50);
                    iv.setMinimumWidth(50);
                    iv.setMaxHeight(50);
                    iv.setMinimumHeight(50);

                    iv.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Saat: " + tahmin.getSaat() +
                                    ", Rüzgar Yönü: " + tahmin.getDalgaRuzgarYonu(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    switch (kisaRuzgarYon) {
                        case "Sakin":
                            iv.setImageResource(R.drawable.sakin);
                            break;
                        case "N":
                            iv.setImageResource(R.drawable.n);
                            break;
                        case "W":
                            iv.setImageResource(R.drawable.w);
                            break;
                        case "E":
                            iv.setImageResource(R.drawable.e);
                            break;
                        case "S":
                            iv.setImageResource(R.drawable.s);
                            break;
                        case "NE":
                            iv.setImageResource(R.drawable.ne);
                            break;
                        case "NW":
                            iv.setImageResource(R.drawable.nw);
                            break;
                        case "SE":
                            iv.setImageResource(R.drawable.se);
                            break;
                        case "SW":
                            iv.setImageResource(R.drawable.sw);
                            break;
                        default:
                            iv.setImageResource(R.drawable.ic_launcher);
                            break;
                    }
                    row.addView(iv);
                }
                break;
            case "DT-Rüzgar Hızı":
                for (final Tahmin tahmin : gunTahminleri) {
                    String ruzgarHizi = tahmin.getDalgaRuzgarHizi();
                    TextView tv = new TextView(context);
                    tv.setWidth(50);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setText(kisaHalAl(ruzgarHizi));
                    tv.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Saat: " + tahmin.getSaat() +
                                    "Rüzgar Hızı:" + tahmin.getDalgaRuzgarHizi(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    row.addView(tv);
                }
                break;
            case "DT-Dalga Yönü":
                for(final Tahmin tahmin : gunTahminleri) {
                    String ruzgarYonu = tahmin.getDalgaYonu();
                    String kisaRuzgarYon;
                    if(!ruzgarYonu.equals("Sakin")) {
                        kisaRuzgarYon  = kisaHalAl(ruzgarYonu);
                    }else {
                        kisaRuzgarYon = "Sakin";
                    }
                    ImageView iv = new ImageView(context);
                    iv.setMaxWidth(50);
                    iv.setMinimumWidth(50);
                    iv.setMaxHeight(50);
                    iv.setMinimumHeight(50);

                    iv.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "Saat: " + tahmin.getSaat() +
                                    ", Rüzgar Yönü: " + tahmin.getDalgaYonu(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    switch (kisaRuzgarYon) {
                        case "Sakin":
                            iv.setImageResource(R.drawable.sakin);
                            break;
                        case "N":
                            iv.setImageResource(R.drawable.n);
                            break;
                        case "W":
                            iv.setImageResource(R.drawable.w);
                            break;
                        case "E":
                            iv.setImageResource(R.drawable.e);
                            break;
                        case "S":
                            iv.setImageResource(R.drawable.s);
                            break;
                        case "NE":
                            iv.setImageResource(R.drawable.ne);
                            break;
                        case "NW":
                            iv.setImageResource(R.drawable.nw);
                            break;
                        case "SE":
                            iv.setImageResource(R.drawable.se);
                            break;
                        case "SW":
                            iv.setImageResource(R.drawable.sw);
                            break;
                        default:
                            iv.setImageResource(R.drawable.ic_launcher);
                            break;
                    }
                    row.addView(iv);
                }
                break;
            case "DT-Dalga Yüksekliği":
                for (final Tahmin tahmin : gunTahminleri) {
                    Double dalgaYuksekligi = tahmin.getDalgaYuksekligi();
                    TextView tv = new TextView(context);
                    tv.setWidth(50);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    tv.setText(String.valueOf(dalgaYuksekligi));
                    row.addView(tv);

                }
                break;

        }
        if(bilgi.contains("DT-")) {
            dalgaTahminTableLayout.addView(row);
        }else {
            marinaTahminTableLayout.addView(row);
        }

    }

    public String kisaHalAl(String s) {
        return s.split(" ")[0];
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
