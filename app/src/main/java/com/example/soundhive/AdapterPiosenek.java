package com.example.soundhive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterPiosenek extends RecyclerView.Adapter<AdapterPiosenek.PiosenkaViewHolder> {

    private Context kontekst;
    private ArrayList<Piosenka> listaPiosenek;
    private OnPiosenkaListener onPiosenkaListener;

    public interface OnPiosenkaListener {
        void onPiosenkaClick(int pozycja);
    }

    public AdapterPiosenek(Context kontekst, ArrayList<Piosenka> listaPiosenek, OnPiosenkaListener onPiosenkaListener) {
        this.kontekst = kontekst;
        this.listaPiosenek = listaPiosenek;
        this.onPiosenkaListener = onPiosenkaListener;
    }

    @NonNull
    @Override
    public PiosenkaViewHolder onCreateViewHolder(@NonNull ViewGroup rodzic, int viewType) {
        View widok = LayoutInflater.from(kontekst).inflate(R.layout.element_listy_piosenek, rodzic, false);
        return new PiosenkaViewHolder(widok, onPiosenkaListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PiosenkaViewHolder holder, int pozycja) {
        Piosenka biezacaPiosenka = listaPiosenek.get(pozycja);

        holder.tekstTytul.setText(biezacaPiosenka.getTytul());
        holder.tekstWykonawca.setText(biezacaPiosenka.getWykonawca());
        holder.tekstAlbum.setText(biezacaPiosenka.getAlbum());
        holder.obrazOkładka.setImageResource(biezacaPiosenka.getOkładka());
    }

    @Override
    public int getItemCount() {
        return listaPiosenek.size();
    }

    public static class PiosenkaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView obrazOkładka;
        public TextView tekstTytul;
        public TextView tekstWykonawca;
        public TextView tekstAlbum;
        OnPiosenkaListener onPiosenkaListener;

        public PiosenkaViewHolder(@NonNull View itemView, OnPiosenkaListener onPiosenkaListener) {
            super(itemView);
            obrazOkładka = itemView.findViewById(R.id.okladkaImageView);
            tekstTytul = itemView.findViewById(R.id.tytulTextView);
            tekstWykonawca = itemView.findViewById(R.id.wykonawcaTextView);
            tekstAlbum = itemView.findViewById(R.id.albumTextView);
            this.onPiosenkaListener = onPiosenkaListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPiosenkaListener.onPiosenkaClick(getAdapterPosition());
        }
    }
}
