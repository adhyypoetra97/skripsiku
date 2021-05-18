package com.spk.dispepsia.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.common.primitives.Ints;
import com.spk.dispepsia.Database.AturanDB;
import com.spk.dispepsia.Database.DiagnosaDB;
import com.spk.dispepsia.Database.GejalaDB;
import com.spk.dispepsia.Database.JenisPenyakitDB;
import com.spk.dispepsia.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Diagnosa extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    GejalaDB gejalaDB;
    JenisPenyakitDB jenisPenyakitDB;
    AturanDB aturanDB;
    TextView txHasil;
    DiagnosaDB diagnosaDB;
    List<String> ci = new ArrayList<>();
    List<String> nn = new ArrayList<>();
    List<Integer> penting = new ArrayList<>();
    List<Integer> bb = new ArrayList<>();
    List<Integer> alt1 = new ArrayList<>();
    List<Integer> alt2 = new ArrayList<>();
    List<Integer> alt3 = new ArrayList<>();
    List<Integer> A1 = new ArrayList<>();
    List<Integer> A2 = new ArrayList<>();
    List<Integer> A3 = new ArrayList<>();
    List<Integer> nilai_a1 = new ArrayList<>();
    List<Integer> nilai_a2 = new ArrayList<>();
    List<Integer> nilai_a3 = new ArrayList<>();
    String[] kriteria = null;
    int[] bobot = null;
    String[] alternatif = null;
    int[] a1 = null;
    int[] a2 = null;
    int[] a3 = null;
    int[] alter1 = null;
    int[] alter2;
    int[] alter3;
    int[][] alternatifkriteria;
    int[] nilai;
    int[] posBagi;
    int[][] nilaiAlKri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosa);

        Toolbar toolbar = findViewById(R.id.toolbardiagnosa);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Diagnosa");

        diagnosaDB = new DiagnosaDB(getBaseContext());
        pemeriksaan periksa = new pemeriksaan();

        diagnosaDB = new DiagnosaDB(this);
        gejalaDB = new GejalaDB(this);
        jenisPenyakitDB = new JenisPenyakitDB(this);
        aturanDB = new AturanDB(this);

        listView = (ListView)findViewById(R.id.list_diagnosa);
        txHasil = (TextView) findViewById(R.id.editHasil);
        listView.setOnItemClickListener(this);

        Button proses = (Button)findViewById(R.id.btn_prosesdiagnosa);

        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alternatif
                Cursor cr = jenisPenyakitDB.allData();
                nn = jenisPenyakitDB.alternatif();
                if(cr.moveToNext()){
                    int jml = cr.getColumnCount();
                    for (int i=0; i-1<jml-1; i++){
                        alternatif = nn.toArray(new String[i]);
                    }
                }

                //Kriteria
                for (int i=0; i<listView.getCount(); i++){
                    if (listView.isItemChecked(i)){
                        ci.add(listView.getItemAtPosition(i).toString().substring(0,3));
                        for (int j=0; j-1<listView.getCheckedItemCount()-1; j++){
                            kriteria = ci.toArray(new String[j]);
                        }
                    }
                }

                //Bobot
                for (int i=0; i<listView.getCount(); i++){
                    if (listView.isItemChecked(i)){
                        String data = listView.getItemAtPosition(i).toString();
                        int a = data.length()-2;
                        String b = listView.getItemAtPosition(i).toString().substring(a);
                        bb.add(Integer.parseInt(b.substring(0,1)));
                        for (int j=0; j-1<listView.getCheckedItemCount()-1; j++){
                            bobot = Ints.toArray(bb);
                        }
                    }
                }

                //Nilai Atribut
                alternatifkriteria = new int[alternatif.length][kriteria.length];
                for (int i=0; i<alternatif.length; i++){
                    for (int k=0; k<kriteria.length; k++){
                        String alt = alternatif[i];
                        String kri = kriteria[k];
                        Cursor cur = aturanDB.altkri(alt, kri);
                        if (cur.moveToNext()){
                            int nilai = Integer.parseInt(cur.getString(cur.getColumnIndex(AturanDB.row_nilai)));
                            alternatifkriteria[i][k] = nilai;
                        }
                    }
                }

                //Pembagi
                double[] pembagi = new double[kriteria.length];
                for (int i=0; i<kriteria.length; i++){
                    pembagi[i] = 0;
                    for (int j=0; j<alternatif.length; j++){
                        pembagi[i] = pembagi[i] + (alternatifkriteria[j][i] * alternatifkriteria[j][i]);
                    }
                    pembagi[i] = Math.sqrt(pembagi[i]);
                }

                //Normalisasi Matriks
                double[][] normalisasi = new double[alternatif.length][kriteria.length];
                for (int i=0; i<nn.size(); i++){
                    for (int j=0; j<kriteria.length; j++){
                        normalisasi[i][j] = alternatifkriteria[i][j] / pembagi[j];
                    }
                }

                //Normalisasi Terbobot
                double[][] terbobot = new double[alternatif.length][kriteria.length];
                for (int i=0; i<alternatif.length; i++){
                    for (int j=0; j<kriteria.length; j++){
                        terbobot[i][j] = normalisasi[i][j] * bobot[j];
                    }
                }

                //A+
                double[] aplus = new double[kriteria.length];
                for (int i=0; i<kriteria.length; i++){
                    for (int j=0; j<alternatif.length; j++){
                        if ((j==0) || (aplus[i] < terbobot[j][i])) {
                            aplus[i] = terbobot[j][i];
                        }
                    }
                }

                //A-
                double[] amin = new double[kriteria.length];
                for (int i=0; i<kriteria.length; i++){
                    for (int j=0; j<alternatif.length; j++){
                        if ((j==0) || (amin[i] > terbobot[j][i])) {
                            amin[i] = terbobot[j][i];
                        }
                    }
                }

                //D+
                double[] dplus = new double[alternatif.length];
                for (int m=0; m<alternatif.length; m++) {
                    dplus[m] = 0;
                    for (int n=0; n<kriteria.length; n++){
                        dplus[m] = dplus[m] + ((terbobot[m][n] - aplus[n]) * (terbobot[m][n] - aplus[n]));
                    }
                    dplus[m] = Math.sqrt(dplus[m]);
                }

                //D-
                double[] dmin = new double[alternatif.length];
                for (int i=0; i<alternatif.length; i++) {
                    dmin[i] = 0;
                    for (int j=0; j<kriteria.length; j++){
                        dmin[i] = dmin[i] + ((terbobot[i][j] - amin[j]) * (terbobot[i][j] - amin[j]));
                    }
                    dmin[i] = Math.sqrt(dmin[i]);
                }

                //Preferensi
                double[] hasil = new double[alternatif.length];
                for (int i=0; i<alternatif.length; i++){
                    hasil[i] = dmin[i] / (dmin[i] + dplus[i]);
                }

                listView.setVisibility(View.GONE);
                txHasil.setVisibility(View.VISIBLE);
                proses.setVisibility(View.GONE);
                txHasil.setText(//"Alternatif : "+Arrays.toString(alternatif)+"\n\n"+
                      //  "Kriteria : "+Arrays.toString(kriteria)+"\n\n"+
                    //   "Bobot : "+Arrays.toString(bobot)+"\n\n"+
                       "Nilai Atribut: "+Arrays.deepToString(alternatifkriteria)+"\n\n"+
                      //  "Pembagi: "+Arrays.toString(pembagi)+"\n\n"+
                        "Normalisasi: "+Arrays.deepToString(normalisasi)+"\n\n"+
                       "Terbobot: "+Arrays.deepToString(terbobot)+"\n\n"+
                       "A+: "+Arrays.toString(aplus)+"\n\n"+
                       "A-: "+Arrays.toString(amin)+"\n\n"+ "D+: "+Arrays.toString(dplus)+"\n\n"+
                        "D-: "+Arrays.toString(dmin)+"\n\n"+
                        "Preferensi: "+Arrays.toString(hasil)+"\n\n");



            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_diagnosa, menu);
        return true;
    }

    public void setListView(){
        /*Cursor cursor = gejalaDB.allData();
        DiagnosaAdapter customCursorAdapter = new DiagnosaAdapter(this, cursor, 1);
        listView.setAdapter(customCursorAdapter); */

        List<String> data = gejalaDB.ambilData();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, data);
        listView.setAdapter(dataAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setListView();
    }
}