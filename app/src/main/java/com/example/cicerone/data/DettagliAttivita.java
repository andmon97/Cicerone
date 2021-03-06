package com.example.cicerone.data;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cicerone.R;

import java.util.ArrayList;

public class DettagliAttivita extends AppCompatActivity {
    private String chiamante;
    private TextView description;
    private Button feedback;
    private ArrayList<Prenotazione> p;
    private String emailcic="";
    private TextView hour;
    private TextView date;
    private TextView city;
    private TextView tongue;
    private TextView npartecipanti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_attivita);

        description = findViewById(R.id.description);
        city = findViewById(R.id.city);
        date = findViewById(R.id.date);
        hour = findViewById(R.id.hour);
        tongue = findViewById(R.id.tongue);
        npartecipanti = findViewById(R.id.npartecipanti);
        TextView npartecipantitxt = findViewById(R.id.npartecipantitxt);
        TextView cicerone = findViewById(R.id.cicerone);
        TextView ciceronetxt = findViewById(R.id.ciceronetxt);
        Button rimuovi = findViewById(R.id.rimozione);
        TextView alert = findViewById(R.id.alert);
        feedback = findViewById(R.id.feedback);

        final Integer id = getIntent().getExtras().getInt("id");

        final Attivita a = DBhelper.getAttivita(id);
        final Utente u = DBhelper.getInfoUtentebyID(DettagliAttivita.this,a.getCicerone());
        emailcic= u.getEmail();

        chiamante = getIntent().getExtras().getString("chiamante");

        inizializza(rimuovi,alert,npartecipantitxt,npartecipanti,cicerone,ciceronetxt,a);

        city.setText(a.getCitta());
        date.setText(a.getData());
        hour.setText(a.getOra().toString());

        ArrayList<Lingua> l = DBhelper.getAllLingue();

        String lingua="";
        for(Lingua l2:l){
            if(l2.getId().equals(a.getLingua()))
                lingua=l2.getNome();
        }

        tongue.setText(lingua);

        p = DBhelper.getAllPrenotazioni(id,chiamante);

        rimuovi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottone(chiamante,a,id,u);
            }
        });
    }

    private void inizializza(Button rimuovi, TextView alert, TextView npartecipantitxt, TextView npartecipanti, TextView cicerone, TextView ciceronetxt, final Attivita a){
        Utente utente = DBhelper.getInfoUtentebyID(DettagliAttivita.this,a.getCicerone());
        String email = utente.getEmail();

        if(chiamante.equals("cerca")) {
            rimuovi.setText("Partecipa");
            alert.setText("Inoltrando la richiesta di partecipazione bisogna aspettare di essere accettati dal Cicerone.");
            npartecipantitxt.setText("Posti disponibili:");
            npartecipanti.setText(""+getIntent().getExtras().getInt("postidisponibili"));
            description.setText(a.getDescrizione());

            cicerone.setText(email);

            feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inte = new Intent(DettagliAttivita.this,ElencoFeedback.class);
                    inte.putExtra("id",a.getCicerone());
                    startActivity(inte);
                }
            });

        }
        if(chiamante.equals("modifica")) {
            npartecipanti.setText(""+a.getMaxPartecipanti());
            cicerone.setVisibility(View.INVISIBLE);
            ciceronetxt.setVisibility(View.INVISIBLE);
            feedback.setVisibility(View.INVISIBLE);
            description.setText(a.getDescrizione());
        }
        if(chiamante.equals("inoltrate")) {
            rimuovi.setText("Annulla prenotazione");
            alert.setVisibility(View.INVISIBLE);
            feedback.setVisibility(View.INVISIBLE);
            npartecipantitxt.setText("Posti prenotati:");
            npartecipanti.setText(""+getIntent().getExtras().getInt("prenotati"));
            cicerone.setText(email);
            if(getIntent().getExtras().getInt("flag")==0)
                description.setText(a.getDescrizione());
            else description.setText(getIntent().getExtras().getString("descrizione"));
        }
    }

    private void bottone(String chiamante,Attivita a,Integer id,Utente u){
        if(chiamante.equals("modifica")){
            DBhelper.rimuoviAttivita(a.getIdAttivita());
            Toast.makeText(DettagliAttivita.this, "Rimozione completata.", Toast.LENGTH_SHORT).show();

            for(Prenotazione p2:p){
                String email = DBhelper.getInfoUtentebyID(DettagliAttivita.this,p2.getId()).getEmail();

                String subject = "Rimozione attività";
                String corpo = "Ciao!\n\nPurtroppo il Cicerone "+a.getCicerone()+" ha rimosso la sua attività n "+a.getIdAttivita()+
                            " che si sarebbe svolta a "+a.getCitta()+" il "+a.getData()+".\n\nIl team Step di Cicerone.";
                SendIt sendIt = new SendIt(email,subject,corpo,this);
                sendIt.execute();
                finish();
            }
        }

        if(chiamante.equals("cerca")) {
            //richiesta di partecipazione
            int partecipanti = getIntent().getExtras().getInt("npartecipanti");
            Integer idUtente = getIntent().getExtras().getInt("idUtente");
            String email = DBhelper.getInfoUtentebyID(DettagliAttivita.this,idUtente).getEmail();

            if (DBhelper.richiestaPartecipazione(partecipanti,id,idUtente)==-1)
                Toast.makeText(DettagliAttivita.this, "Errore nell'inoltro della richiesta.", Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(DettagliAttivita.this, "Richiesta inoltrata.", Toast.LENGTH_SHORT).show();
                String subject = "Richiesta di partecipazione";
                String corpo = "Ciao!\n\nIl Globetrotter "+email+" vorrebbe partecipare all'attività n "+a.getIdAttivita()+
                        " che si svolge a "+a.getCitta()+" il "+a.getData()+".\nCorri nella sezione 'Gestione richieste'"+
                        " e fai la tua scelta.\n\nIl team Step di Cicerone.";
                SendIt sendIt = new SendIt(emailcic,subject,corpo,this);
                sendIt.execute();
                finish();
            }
        }
        if(chiamante.equals("inoltrate")){
            if(DBhelper.rimuoviPrenotazione(a.getIdAttivita())==1)
                Toast.makeText(DettagliAttivita.this, "Errore nell'annullamento.", Toast.LENGTH_SHORT).show();
            else {
                String email = getIntent().getExtras().getString("email");
                Toast.makeText(DettagliAttivita.this, "Annullamento completato.", Toast.LENGTH_SHORT).show();
                String subject = "Annullamento prenotazione";
                String corpo = "Ciao!\n\nL'utente "+email+" ha rimosso la sua prenotazione dall'attività n "+a.getIdAttivita()+
                        " che si svolge a "+a.getCitta()+" il "+a.getData()+".\n\nIl team Step di Cicerone.";
                SendIt sendIt = new SendIt(u.getEmail(),subject,corpo,this);
                sendIt.execute();
                finish();
            }
        }
    }
}
