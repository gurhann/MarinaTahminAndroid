package com.kayra.marinatahmin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.kayra.marinatahmin.model.Liman;

import java.util.ArrayList;


public class MainActivity extends android.support.v7.app.ActionBarActivity {

    private ArrayList<Liman> limanlar = new ArrayList<>();
    private Spinner limanlarSpinner;
    private TableLayout anaTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        limanListesiniOlustur();
        limanlarSpinner = (Spinner) findViewById(R.id.limanlarSpinner);
        String [] limanDizi = new String[limanlar.size()];
        for(int i = 0; i < limanDizi.length; i++) {
            limanDizi[i] = limanlar.get(i).toString();
        }
        ArrayAdapter<String> adapter  =new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, limanDizi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        limanlarSpinner.setAdapter(adapter);

        limanlarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new TahminleriAlAsyncTask(MainActivity.this, limanlar.get(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void limanListesiniOlustur() {
        limanEkle(17612, "AKÇAKOCA");
        limanEkle(17310, "ALANYA");
        limanEkle(17602, "AMASRA");
        limanEkle(17320, "ANAMUR");
        limanEkle(17300, "ANTALYA");
        limanEkle(17060, "ATAKÖY");
        limanEkle(17175, "AYVALIK");
        limanEkle(17115, "BANDIRMA");
        limanEkle(17290, "BODRUM");
        limanEkle(17990, "YALIKAVAK (Bodrum)");
        limanEkle(17111, "BOZCAADA");
        limanEkle(17112, "ÇANAKKALE");
        limanEkle(17221, "ÇEŞME");
        limanEkle(17233, "DİDİM");
        limanEkle(17611, "EREĞLİ (Karadeniz)");
        limanEkle(17296, "ECE (Fethiye)");
        limanEkle(17375, "FİNİKE");
        limanEkle(17034, "GİRESUN");
        limanEkle(17067, "GÖLCÜK");
        limanEkle(17042, "HOPA");
        limanEkle(17024, "İNEBOLU");
        limanEkle(17370, "İSKENDERUN");
        limanEkle(17220, "İZMİR");
        limanEkle(17380, "KAŞ");
        limanEkle(17907, "KALAMIŞ");
        limanEkle(17953, "KEMER");
        limanEkle(17232, "KUŞADASI");
        limanEkle(17991, "YACHT (Marmaris)");
        limanEkle(17340, "MERSİN");
        limanEkle(17033, "ORDU");
        limanEkle(17040, "RİZE");
        limanEkle(17031, "SAMSUN");
        limanEkle(17330, "TAŞUCU");
        limanEkle(17056, "TEKİRDAĞ");
        limanEkle(17038, "TRABZON");
        limanEkle(17627, "TURGUT REİS");
        limanEkle(17026, "SİNOP");
        limanEkle(17610, "ŞİLE");
        limanEkle(17119, "YALOVA");
        limanEkle(17979, "YUMURTALIK");
        limanEkle(17022, "ZONGULDAK");
        limanEkle(17540, "GAZİMAGOSA (K.K.T.C.)");
        limanEkle(17510, "GİRNE (K.K.T.C.)");
        limanEkle(16749, "RODOS (Yunanistan)");
        limanEkle(16667, "MİDİLLİ (Yunanistan)");




    }

    private void limanEkle(int id, String ad) {
        Liman liman = new Liman(id, ad);
        limanlar.add(liman);
    }
}
