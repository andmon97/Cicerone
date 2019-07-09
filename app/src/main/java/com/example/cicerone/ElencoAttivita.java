package com.example.cicerone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cicerone.data.model.DBhelper;

import java.util.ArrayList;

public class ElencoAttivita extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elenco_attivita);

        final String chiamante = getIntent().getExtras().getString("chiamante");
        ArrayAdapter<String> adapter=null;
        FoodAdapter fadapter=null;
        TextView partecipantitxt = findViewById(R.id.partecipantitxt);

        ListView lista = findViewById(R.id.listaAttivita);

        ArrayList<String> array = new ArrayList<>(); //visualizzati nella lista
        ArrayList<Attivita> s= new ArrayList<>();
        ArrayList<Prenotazione> p = new ArrayList<>();

        if(chiamante.equals("inoltrate")){
            String email = getIntent().getExtras().getString("id");
            p = new DBhelper(ElencoAttivita.this).getAllPrenotazioniUtente(email);
            for(Prenotazione p2:p)
                s.add(new DBhelper(ElencoAttivita.this).getAttivita(p2.getIdAttivita()));
        }
        else s = new DBhelper(ElencoAttivita.this).getAllAttivita(getIntent().getExtras().getString("id"));

        ArrayList<Integer> r= new ArrayList<>(); //n richieste per attività

        String avv=""; //non ci sono attività
        //array degli id
        final Integer[] ids;

        if(chiamante.equals("richieste"))
            partecipantitxt.setText("Richieste");
        if(chiamante.equals("inoltrate"))
            partecipantitxt.setText("Stato");

        int j=0;
        int flag=0; //0 se ci sono attività da mostrare, 1 altrimenti.

        if(s.size()==0) {
            if(chiamante.equals("inoltrate")){
                avv = "Nessuna richiesta inoltrata";
                Toast.makeText(ElencoAttivita.this, "Nessuna richiesta inoltrata", Toast.LENGTH_SHORT).show();
            }
            else{
                avv = "Nessuna attività creata";
                Toast.makeText(ElencoAttivita.this, "Nessuna attività creata", Toast.LENGTH_SHORT).show();
            }
            lista.setClickable(false);
            flag=1;
        }


        if (flag == 0) {
                ids = new Integer[s.size()];
                for (Attivita b : s) {
                    array.add(b.toStringSearch());
                    ids[j] = b.getIdAttivita();
                    ArrayList<Prenotazione> p2 = new DBhelper(ElencoAttivita.this).getAllPrenotazioni(ids[j], chiamante);
                    r.add(p.size());
                    j++;
                }
        }
        else {
            ids = new Integer[1];
            array.add(avv);
        }


        adapter = new ArrayAdapter<>(
                ElencoAttivita.this, android.R.layout.simple_list_item_1, array
        );

        if(chiamante.equals("modifica"))
            fadapter = new FoodAdapter(this,s,chiamante);
        if(chiamante.equals("richieste"))
            fadapter = new FoodAdapter(this,s,r,chiamante);
        if(chiamante.equals("inoltrate"))
            fadapter = new FoodAdapter(this,s,chiamante,p);

        final ArrayList<Prenotazione> pf = p;

        if (flag==0){
            lista.setAdapter(fadapter);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent inte=null;
                    if(chiamante.equals("modifica"))
                      inte= new Intent(ElencoAttivita.this, DettagliAttivita.class);
                    if(chiamante.equals("richieste"))
                        inte= new Intent(ElencoAttivita.this,DettaglioRichieste.class);
                    if(chiamante.equals("inoltrate")){
                        inte = new Intent(ElencoAttivita.this, DettagliAttivita.class);
                        inte.putExtra("prenotati",pf.get(position).getPartecipanti());
                    }

                    inte.putExtra("id",ids[position]);
                    inte.putExtra("chiamante",chiamante);
                    startActivity(inte);
                    finish();
                }
        });
        }
        else lista.setAdapter(adapter);
    }
}
