package com.example.soundhive; // Deklaracja pakietu

// Importy niezbędnych klas
import android.content.Context; // Udostępnia informacje o środowisku aplikacji, potrzebne m.in. do LayoutInflater
import android.view.LayoutInflater; // Służy do "nadmuchiwania" (tworzenia) widoków z plików XML layoutu
import android.view.View; // Podstawowa klasa dla wszystkich komponentów UI
import android.view.ViewGroup; // Kontener dla innych widoków, RecyclerView jest jego podklasą
import android.widget.ImageView; // Komponent do wyświetlania obrazów
import android.widget.TextView; // Komponent do wyświetlania tekstu

import androidx.annotation.NonNull; // Adnotacja wskazująca, że parametr/wynik nie powinien być null
import androidx.recyclerview.widget.RecyclerView; // Klasa bazowa dla adaptera

import java.util.ArrayList; // Implementacja dynamicznej listy

// Deklaracja klasy AdapterPiosenek
// Dziedziczy po RecyclerView.Adapter, co jest wymagane do stworzenia adaptera dla RecyclerView.
// <AdapterPiosenek.PiosenkaViewHolder> określa typ ViewHoldera, który będzie używany przez ten adapter.
public class AdapterPiosenek extends RecyclerView.Adapter<AdapterPiosenek.PiosenkaViewHolder> {

    // --- Pola klasy ---
    private Context kontekst; // Kontekst aplikacji lub aktywności, potrzebny np. do LayoutInflater
    private ArrayList<Piosenka> listaPiosenek; // Lista obiektów Piosenka, które adapter ma wyświetlić
    private OnPiosenkaListener onPiosenkaListener; // Referencja do obiektu nasłuchującego na kliknięcia

    // --- Interfejs do obsługi kliknięć ---
    // Definiuje "kontrakt" dla klasy, która chce być powiadamiana o kliknięciu na element listy.
    // MainActivity będzie implementować ten interfejs.
    public interface OnPiosenkaListener {
        void onPiosenkaClick(int pozycja); // Metoda, która zostanie wywołana po kliknięciu, przekazując pozycję klikniętego elementu
    }

    // --- Konstruktor adaptera ---
    public AdapterPiosenek(Context kontekst, ArrayList<Piosenka> listaPiosenek, OnPiosenkaListener onPiosenkaListener) {
        this.kontekst = kontekst; // Przypisanie przekazanego kontekstu
        this.listaPiosenek = listaPiosenek; // Przypisanie przekazanej listy piosenek
        this.onPiosenkaListener = onPiosenkaListener; // Przypisanie przekazanego listenera
    }

    // --- Metody wymagane przez RecyclerView.Adapter ---

    // Wywoływana, gdy RecyclerView potrzebuje nowego ViewHolder'a do wyświetlenia elementu.
    // Tworzy i zwraca nowy obiekt PiosenkaViewHolder.
    @NonNull // Wynik tej metody nie powinien być null
    @Override
    public PiosenkaViewHolder onCreateViewHolder(@NonNull ViewGroup rodzic, int viewType) {
        // "Nadmuchiwanie" (tworzenie) widoku pojedynczego elementu listy z pliku XML layoutu.
        // R.layout.element_listy_piosenek to plik XML definiujący wygląd jednego wiersza na liście.
        // rodzic (parent) to RecyclerView, do którego ten element zostanie dodany.
        // false oznacza, że widok nie jest od razu dołączany do rodzica (RecyclerView zrobi to sam).
        View widok = LayoutInflater.from(kontekst).inflate(R.layout.element_listy_piosenek, rodzic, false);
        // Zwraca nowy obiekt PiosenkaViewHolder, przekazując mu stworzony widok i listenera kliknięć.
        return new PiosenkaViewHolder(widok, onPiosenkaListener);
    }

    // Wywoływana, gdy RecyclerView chce wyświetlić dane dla elementu na określonej pozycji.
    // Łączy dane z obiektu Piosenka z widokami w PiosenkaViewHolder.
    @Override
    public void onBindViewHolder(@NonNull PiosenkaViewHolder holder, int pozycja) {
        // Pobranie obiektu Piosenka dla bieżącej pozycji z listy.
        Piosenka biezacaPiosenka = listaPiosenek.get(pozycja);

        // Ustawienie danych w odpowiednich komponentach UI (TextView, ImageView) w ViewHolderze.
        holder.tekstTytul.setText(biezacaPiosenka.getTytul());
        holder.tekstWykonawca.setText(biezacaPiosenka.getWykonawca());
        holder.tekstAlbum.setText(biezacaPiosenka.getAlbum());
        // Ustawienie obrazka okładki. Zakładamy, że getOkładka() zwraca ID zasobu drawable.
        holder.obrazOkładka.setImageResource(biezacaPiosenka.getOkładka());
    }

    // Zwraca całkowitą liczbę elementów w liście danych.
    // RecyclerView używa tej informacji do określenia, ile elementów ma wyświetlić.
    @Override
    public int getItemCount() {
        return listaPiosenek.size(); // Zwraca rozmiar listy piosenek
    }

    // --- Wewnętrzna klasa ViewHolder ---
    // ViewHolder przechowuje referencje do widoków (komponentów UI) pojedynczego elementu listy.
    // Dzięki temu unika się wielokrotnego wywoływania findViewById(), co poprawia wydajność.
    // Implementuje View.OnClickListener, aby cały element listy był klikalny.
    public static class PiosenkaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Deklaracje referencji do komponentów UI z pliku element_listy_piosenek.xml
        public ImageView obrazOkładka;
        public TextView tekstTytul;
        public TextView tekstWykonawca;
        public TextView tekstAlbum;
        OnPiosenkaListener onPiosenkaListener; // Referencja do listenera, aby móc go wywołać

        // Konstruktor ViewHoldera
        public PiosenkaViewHolder(@NonNull View itemView, OnPiosenkaListener onPiosenkaListener) {
            super(itemView); // Wywołanie konstruktora klasy nadrzędnej (ViewHolder)
            // Przypisanie referencji do komponentów UI poprzez ich ID zdefiniowane w XML
            // itemView to widok pojedynczego elementu listy (ten "nadmuchany" w onCreateViewHolder)
            obrazOkładka = itemView.findViewById(R.id.okladkaImageView);
            tekstTytul = itemView.findViewById(R.id.tytulTextView);
            tekstWykonawca = itemView.findViewById(R.id.wykonawcaTextView);
            tekstAlbum = itemView.findViewById(R.id.albumTextView);
            this.onPiosenkaListener = onPiosenkaListener; // Przypisanie listenera

            // Ustawienie listenera kliknięć dla całego widoku elementu (itemView)
            // 'this' oznacza, że metoda onClick() z tej klasy (PiosenkaViewHolder) będzie wywołana
            itemView.setOnClickListener(this);
        }

        // Metoda wywoływana, gdy element listy zostanie kliknięty (bo PiosenkaViewHolder implementuje View.OnClickListener)
        @Override
        public void onClick(View v) {
            // Wywołanie metody onPiosenkaClick z interfejsu OnPiosenkaListener
            // (czyli metody zaimplementowanej w MainActivity).
            // getAdapterPosition() zwraca aktualną pozycję elementu w adapterze.
            // Jest to bezpieczniejszy sposób uzyskania pozycji niż używanie pozycji przekazanej do onBindViewHolder,
            // zwłaszcza jeśli lista może się dynamicznie zmieniać.
            onPiosenkaListener.onPiosenkaClick(getAdapterPosition());
        }
    }
}