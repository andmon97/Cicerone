package com.example.cicerone.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cicerone.R;

import java.util.ArrayList;

public class EAdapter extends BaseAdapter {
    private static final String DETTAGLIO = "dettaglio";

    private Context context;
    private ArrayList<Attivita> a;
    private ArrayList<Integer> nRichieste;
    private ArrayList<Prenotazione> p;
    private ArrayList<Utente> u;
    private ArrayList<Boolean> f;
    private String chiamante;
    private Integer id;

    public EAdapter(Context context, ArrayList<Attivita> a, String chiamante) {
        this.context = context;
        this.a = a;
        this.chiamante = chiamante;
    }

    public EAdapter(Context context, ArrayList<Attivita> a, ArrayList<Integer> r, String chiamante) {
        this.context = context;
        this.a = a;
        this.nRichieste = r;
        this.chiamante = chiamante;
    }

    public EAdapter(Context context, Integer id, ArrayList<Prenotazione> p, ArrayList<Utente> u, String chiamante) {
        this.context = context;
        this.id=id;
        this.p=p;
        this.u=u;
        this.chiamante = chiamante;
    }

    public EAdapter(Context context, ArrayList<Attivita> a, String chiamante, ArrayList<Prenotazione> p) {
        this.context = context;
        this.a = a;
        this.chiamante = chiamante;
        this.p = p;
    }

    public EAdapter(ArrayList<Attivita> a, Context context, ArrayList<Boolean> f, String chiamante) {
        this.context = context;
        this.a = a;
        this.f = f;
        this.chiamante = chiamante;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if(chiamante.equals(DETTAGLIO))
            return p.size();
        else return a.size();
    }

    @Override
    public Object getItem(int position) {
        if(chiamante.equals(DETTAGLIO))
            return p.get(position);
        else return a.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.multiple_columns, null, true);

            holder.id = convertView.findViewById(R.id.id);
            holder.citta = convertView.findViewById(R.id.citta);
            holder.data = convertView.findViewById(R.id.data);
            holder.partecipanti = convertView.findViewById(R.id.partecipanti);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        if(chiamante.equals(DETTAGLIO)){
            holder.id.setText("" + id);
            holder.citta.setText(u.get(position).getNome()+" "+u.get(position).getCognome());
            holder.data.setText(u.get(position).getEmail());
            holder.partecipanti.setText(""+p.get(position).getPartecipanti());
        }
        else {
            holder.id.setText("" + a.get(position).getIdAttivita());
            holder.citta.setText(a.get(position).getCitta());
            holder.data.setText(a.get(position).getData());

            if (chiamante.equals("richieste"))
                holder.partecipanti.setText("" + nRichieste.get(position));
            if (chiamante.equals("modifica"))
                holder.partecipanti.setText("" + a.get(position).getMaxPartecipanti());
            if (chiamante.equals("cerca")) {
                int h = a.get(position).getMaxPartecipanti() - nRichieste.get(position);
                holder.partecipanti.setText("" + h);
            }
            if(chiamante.equals("inoltrate")) {
                String esito;

                switch (p.get(position).getFlagConferma()){
                    case 1:
                        esito="Accettata";
                        break;
                    case 2:
                        esito="Rifiutata";
                        break;
                    default://0
                        esito="In sospeso";
                }
                holder.partecipanti.setText(esito);
            }
            if(chiamante.equals("storico")) {
                String res;
                if(f.get(position))
                    res="Sì";
                else res="No";

                holder.partecipanti.setText(res);
            }
            if(chiamante.equals("elencoF")){
                holder.partecipanti.setText(""+nRichieste.get(position));
            }
        }

        return convertView;
    }

    private class ViewHolder {

        TextView id;
        TextView citta;
        TextView data;
        TextView partecipanti;

    }
}