package com.example.soundhive; // Deklaracja pakietu

// Importy niezbędnych klas
import androidx.appcompat.app.AppCompatActivity; // Podstawowa klasa dla aktywności
import androidx.palette.graphics.Palette; // Biblioteka do ekstrakcji kolorów z obrazów (np. okładek)
import android.content.res.ColorStateList; // Do zarządzania kolorami stanów widoków (nieużywane bezpośrednio, ale często związane z kolorami)
import android.graphics.Bitmap; // Reprezentacja obrazu bitmapowego
import android.graphics.BitmapFactory; // Do tworzenia obiektów Bitmap z różnych źródeł (np. zasobów)
import android.graphics.Color; // Klasa do obsługi kolorów
import android.graphics.PorterDuff; // Definiuje tryby mieszania kolorów (używane do kolorowania np. SeekBar)
import android.graphics.RenderEffect; // Do stosowania efektów renderowania (np. rozmycia) na widokach (API 31+)
import android.graphics.Shader; // Używany z RenderEffect do określenia, jak efekt ma być stosowany
import android.graphics.drawable.BitmapDrawable; // Drawable, który rysuje bitmapę
import android.media.MediaPlayer; // Główna klasa do odtwarzania plików audio i wideo
import android.os.Build; // Informacje o wersji systemu Android (używane do warunkowego stosowania RenderEffect)
import android.os.Bundle; // Do przekazywania danych między stanami aktywności
import android.os.Handler; // Umożliwia planowanie zadań do wykonania w przyszłości w wątku UI
import android.view.WindowManager; // Zarządza właściwościami okna aplikacji
import android.widget.ImageView; // Komponent do wyświetlania obrazów
import android.widget.SeekBar; // Pasek postępu, który użytkownik może przesuwać
import android.widget.TextView; // Komponent do wyświetlania tekstu
import android.widget.ToggleButton; // Przycisk, który ma dwa stany (włączony/wyłączony)
import android.graphics.drawable.GradientDrawable; // Drawable, który rysuje gradienty
import android.content.Intent; // Do odbierania danych przekazanych z poprzedniej aktywności
import android.view.View; // Podstawowa klasa dla wszystkich komponentów UI
import java.util.ArrayList; // Implementacja dynamicznej listy
import java.util.Collections; // Klasa narzędziowa do operacji na kolekcjach (np. tasowanie)
import java.util.Random; // Do generowania liczb losowych (dla trybu shuffle)

public class ActivityOdtwarzania extends AppCompatActivity {

    // --- Deklaracje pól klasy (referencje do widoków i zmienne stanu) ---
    private ImageView obrazOkładka, tloImageView; // Okładka piosenki i tło (rozmyta okładka)
    private TextView tekstTytul, tekstAlbum, tekstWykonawca, tekstCzasAktualny, tekstCzasKoncowy; // Pola tekstowe
    private SeekBar seekBar; // Pasek postępu odtwarzania
    private ImageView przyciskPlayPause, przyciskPrev, przyciskNext; // Przyciski kontrolne
    private ToggleButton przyciskRepeat, przyciskShuffle; // Przyciski przełączające tryby
    private MediaPlayer mediaPlayer; // Obiekt odtwarzacza multimediów
    private Handler handler = new Handler(); // Do aktualizacji paska postępu i czasu
    private Piosenka aktualnaPiosenka; // Aktualnie odtwarzany obiekt Piosenka
    private ArrayList<Piosenka> listaPiosenek; // Pełna lista piosenek (może być modyfikowana przez shuffle)
    private ArrayList<Piosenka> oryginalnaListaPiosenek; // Kopia oryginalnej listy, do przywrócenia po wyłączeniu shuffle
    private int aktualnaPozycja; // Indeks aktualnie odtwarzanej piosenki w 'listaPiosenek'
    private int kolorDominujacy; // Dominujący kolor wyekstrahowany z okładki
    private boolean czyOdtwarzanie = true; // Flaga śledząca, czy odtwarzanie jest aktywne
    private boolean czyRepeat = false; // Flaga trybu powtarzania
    private boolean czyShuffle = false; // Flaga trybu losowego odtwarzania
    private Random random = new Random(); // Generator liczb losowych dla shuffle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odtwarzania); // Ustawienie layoutu (plik activity_odtwarzania.xml)

        inicjalizujWidoki(); // Metoda do znalezienia i przypisania wszystkich widoków z layoutu

        // --- Konfiguracja przezroczystych pasków systemowych (identycznie jak w MainActivity) ---
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        // --- Koniec konfiguracji pasków systemowych ---

        // --- Odbieranie danych przekazanych z MainActivity ---
        Intent intent = getIntent(); // Pobranie Intentu, który uruchomił tę aktywność
        // Odczytanie obiektu Piosenka (rzutowanie na Piosenka, bo getSerializableExtra zwraca Serializable)
        // To zadziała, jeśli klasa Piosenka implementuje java.io.Serializable
        aktualnaPiosenka = (Piosenka) intent.getSerializableExtra("piosenka");
        listaPiosenek = (ArrayList<Piosenka>) intent.getSerializableExtra("listaPiosenek");
        oryginalnaListaPiosenek = new ArrayList<>(listaPiosenek); // Stworzenie kopii listy na potrzeby shuffle
        aktualnaPozycja = intent.getIntExtra("pozycja", 0); // Odczytanie pozycji, domyślnie 0

        ustawDanePiosenki(); // Wyświetlenie informacji o pierwszej piosence
        ustawRozmyteTlo(); // Ustawienie rozmytego tła i ekstrakcja kolorów
        inicjalizujMediaPlayer(); // Przygotowanie i uruchomienie odtwarzacza

        // --- Konfiguracja SeekBar ---
        seekBar.setMax(mediaPlayer.getDuration()); // Ustawienie maksymalnej wartości SeekBar na długość piosenki
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) { // Jeśli zmiana pochodzi od użytkownika (przesunięcie suwaka)
                    mediaPlayer.seekTo(progress); // Przewiń piosenkę do wybranej pozycji
                }
                tekstCzasAktualny.setText(formatujCzas(progress)); // Zaktualizuj wyświetlany czas aktualny
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { /* Nie robimy nic specjalnego */ }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { /* Nie robimy nic specjalnego */ }
        });

        // --- Wątek do aktualizacji SeekBar i czasu co sekundę ---
        ActivityOdtwarzania.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) { // Sprawdzenie czy odtwarzacz istnieje i gra
                    int mCurrentPosition = mediaPlayer.getCurrentPosition(); // Pobranie aktualnej pozycji odtwarzania
                    seekBar.setProgress(mCurrentPosition); // Ustawienie postępu na SeekBar
                    tekstCzasAktualny.setText(formatujCzas(mCurrentPosition)); // Aktualizacja tekstu czasu
                }
                handler.postDelayed(this, 1000); // Ponowne uruchomienie tego samego Runnable po 1000 ms (1 sekunda)
            }
        });

        // --- Listenery dla przycisków kontrolnych ---
        przyciskPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                przyciskPlayPause.setImageResource(R.drawable.play); // Zmień ikonę na "play"
                czyOdtwarzanie = false;
            } else {
                mediaPlayer.start();
                przyciskPlayPause.setImageResource(R.drawable.pause); // Zmień ikonę na "pause"
                czyOdtwarzanie = true;
            }
        });

        przyciskNext.setOnClickListener(v -> nastepnaPiosenka());
        przyciskPrev.setOnClickListener(v -> poprzedniaPiosenka());

        przyciskRepeat.setOnClickListener(v -> {
            czyRepeat = przyciskRepeat.isChecked(); // Sprawdź stan ToggleButton
            mediaPlayer.setLooping(czyRepeat); // Ustaw tryb pętli w MediaPlayer
            if (czyRepeat) {
                przyciskRepeat.setBackgroundResource(R.drawable.replay); // Zmień tło przycisku na aktywne
            } else {
                przyciskRepeat.setBackgroundResource(R.drawable.foreward); // Zmień tło na nieaktywne
            }
        });

        przyciskShuffle.setOnClickListener(v -> {
            czyShuffle = przyciskShuffle.isChecked();
            if (czyShuffle) {
                // Tryb losowy włączony
                Collections.shuffle(listaPiosenek); // Potasuj aktualną listę
                // Aby bieżąca piosenka nie zniknęła od razu i była pierwsza w potasowanej liście:
                // Znajdź jej nową pozycję po potasowaniu i przenieś na początek, jeśli to konieczne.
                // Prostsze podejście (użyte w kodzie): usuń starą, dodaj na początek i ustaw pozycję na 0.
                // Jest to nieco nieoptymalne, bo usuwa i dodaje, ale dla małych list jest OK.
                // Lepszym podejściem byłoby znalezienie aktualnej piosenki w potasowanej liście i
                // ustawienie 'aktualnaPozycja' na jej nowy indeks.
                // Kod autora:
                Piosenka tempAktualna = aktualnaPiosenka; // Zapisz referencję do aktualnie granej piosenki
                listaPiosenek.remove(tempAktualna); // Usuń ją z potasowanej listy (jeśli tam jest po ID)
                listaPiosenek.add(0, tempAktualna); // Dodaj ją na początek
                aktualnaPozycja = 0; // Ustaw aktualną pozycję na 0
                przyciskShuffle.setBackgroundResource(R.drawable.shuffle); // Zmień tło przycisku
            } else {
                // Tryb losowy wyłączony
                listaPiosenek = new ArrayList<>(oryginalnaListaPiosenek); // Przywróć oryginalną kolejność
                aktualnaPozycja = listaPiosenek.indexOf(aktualnaPiosenka); // Znajdź pozycję bieżącej piosenki w oryginalnej liście
                przyciskShuffle.setBackgroundResource(R.drawable.order); // Zmień tło przycisku
            }
        });
    }

    // Inicjalizacja referencji do widoków z layoutu XML
    private void inicjalizujWidoki() {
        obrazOkładka = findViewById(R.id.okladkaDuzaImageView);
        tloImageView = findViewById(R.id.tloImageView);
        tekstTytul = findViewById(R.id.tytulDuzyTextView);
        tekstAlbum = findViewById(R.id.albumTextView); // Uwaga: w MainActivity nazwane 'tekstAlbum', tutaj może być inaczej w XML
        tekstWykonawca = findViewById(R.id.wykonawcaDuzyTextView);
        tekstCzasAktualny = findViewById(R.id.czasAktualnyTextView);
        tekstCzasKoncowy = findViewById(R.id.czasKoncowyTextView);
        seekBar = findViewById(R.id.seekBar);
        przyciskPlayPause = findViewById(R.id.pause_play);
        przyciskPrev = findViewById(R.id.previous);
        przyciskNext = findViewById(R.id.next);
        przyciskRepeat = findViewById(R.id.repeatButton);
        przyciskShuffle = findViewById(R.id.shuffleButton);
    }

    // Ustawia rozmyte tło na podstawie okładki i ekstrahuje dominujący kolor
    private void ustawRozmyteTlo() {
        // Dekodowanie zasobu drawable (okładki) do obiektu Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), aktualnaPiosenka.getOkładka());

        // Ustawienie oryginalnej (nierozmytej) bitmapy jako źródło dla tła
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        tloImageView.setImageDrawable(drawable);

        // Zastosowanie efektu rozmycia (Blur) do tloImageView
        // RenderEffect jest dostępny od API 31 (Android 12). Na starszych wersjach to nie zadziała.
        // Warto dodać sprawdzenie: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { ... }
        // 25f, 25f to promień rozmycia w osi X i Y.
        // Shader.TileMode.CLAMP określa, jak piksele na krawędziach mają być traktowane.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Warunek dodany dla poprawności
             tloImageView.setRenderEffect(RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP));
        } else {
            // Dla starszych wersji można użyć alternatywnej metody rozmycia (np. biblioteka RenderScript lub niestandardowy kod)
            // lub po prostu zostawić nierozmyte tło.
            // Tutaj dla uproszczenia zakładamy, że API 31+ jest targetowane lub efekt jest opcjonalny.
        }


        // Ekstrakcja palety kolorów z bitmapy okładki
        Palette.from(bitmap).generate(palette -> { // Asynchroniczne generowanie palety
            Palette.Swatch dominantSwatch = palette.getDominantSwatch(); // Pobranie próbki z dominującym kolorem
            if (dominantSwatch != null) {
                kolorDominujacy = dominantSwatch.getRgb(); // Pobranie wartości RGB koloru
                ustawKoloryInterfejsu(); // Wywołanie metody do zastosowania tego koloru w UI
            } else {
                // Ustawienie domyślnego koloru, jeśli paleta nie znajdzie dominującego
                kolorDominujacy = Color.DKGRAY; // Przykład
                ustawKoloryInterfejsu();
            }
        });
    }

    // Ustawia kolory dynamicznych elementów UI na podstawie wyekstrahowanego 'kolorDominujacy'
    private void ustawKoloryInterfejsu() {
        // Tworzenie gradientu od lekko przezroczystego dominującego koloru do czarnego
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, // Orientacja gradientu
                new int[]{Color.argb(200, Color.red(kolorDominujacy), // Kolor początkowy (dominujący z alfa 200/255)
                        Color.green(kolorDominujacy),
                        Color.blue(kolorDominujacy)),
                        0xFF000000}); // Kolor końcowy (czarny, nieprzezroczysty)
        // Ustawienie gradientu jako tło głównego layoutu aktywności
        findViewById(R.id.activityOdtwarzaniaLayout).setBackground(gradientDrawable); // Zakładając, że główny layout ma ID 'activityOdtwarzaniaLayout'

        // Kolorowanie paska postępu (SeekBar)
        // Ustawia kolor dla części "zapełnionej" paska
        seekBar.getProgressDrawable().setColorFilter(kolorDominujacy, PorterDuff.Mode.SRC_IN);
        // Ustawia kolor dla "kciuka" (suwaka)
        seekBar.getThumb().setColorFilter(kolorDominujacy, PorterDuff.Mode.SRC_IN);

        // Kolorowanie przycisku Play/Pause (jeśli jest to ImageView)
        // Używa filtra kolorów, aby nadać ikonie dominujący kolor
        przyciskPlayPause.setColorFilter(kolorDominujacy);
        // Można by to rozszerzyć na inne przyciski (next, prev), jeśli są ImageView
    }

    // Inicjalizuje lub resetuje MediaPlayer dla aktualnej piosenki
    private void inicjalizujMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Zwolnienie zasobów poprzedniego MediaPlayer, jeśli istniał
        }
        // Tworzenie nowego obiektu MediaPlayer z pliku audio aktualnej piosenki
        mediaPlayer = MediaPlayer.create(this, aktualnaPiosenka.getPlikAudio());
        mediaPlayer.setLooping(czyRepeat); // Ustawienie trybu pętli zgodnie z flagą
        mediaPlayer.setOnCompletionListener(mp -> { // Listener wywoływany po zakończeniu odtwarzania piosenki
            if (!czyRepeat) { // Jeśli tryb powtarzania nie jest włączony
                nastepnaPiosenka(); // Przejdź do następnej piosenki
            }
            // Jeśli czyRepeat jest true, setLooping(true) zajmie się automatycznym zapętleniem.
        });
        tekstCzasKoncowy.setText(formatujCzas(mediaPlayer.getDuration())); // Ustawienie całkowitego czasu trwania piosenki
        if (czyOdtwarzanie) { // Jeśli poprzednio było odtwarzanie (lub startujemy po raz pierwszy)
             mediaPlayer.start(); // Rozpocznij odtwarzanie
             przyciskPlayPause.setImageResource(R.drawable.pause); // Ustaw ikonę na "pause"
        } else {
            // Jeśli poprzednio była pauza (np. po zmianie piosenki, gdy poprzednia była spauzowana)
            przyciskPlayPause.setImageResource(R.drawable.play); // Ustaw ikonę na "play"
            // Nie startujemy odtwarzania automatycznie, jeśli było spauzowane
        }
        seekBar.setProgress(0); // Zresetuj SeekBar na początek
        seekBar.setMax(mediaPlayer.getDuration()); // Upewnij się, że max SeekBara jest aktualny
    }

    // Ustawia dane tekstowe i okładkę dla aktualnej piosenki
    private void ustawDanePiosenki() {
        obrazOkładka.setImageResource(aktualnaPiosenka.getOkładka());
        tekstTytul.setText(aktualnaPiosenka.getTytul());
        tekstAlbum.setText(aktualnaPiosenka.getAlbum());
        tekstWykonawca.setText(aktualnaPiosenka.getWykonawca());
    }

    // Przechodzi do następnej piosenki
    private void nastepnaPiosenka() {
        if (listaPiosenek == null || listaPiosenek.isEmpty()) return; // Zabezpieczenie
        if (czyShuffle) {
            // W trybie losowym wybierz losową pozycję, różną od aktualnej (opcjonalnie)
            // Prosty sposób: wybierz dowolną losową.
            int nowaPozycja;
            if (listaPiosenek.size() > 1) { // Unikaj pętli nieskończonej dla jednej piosenki
                do {
                    nowaPozycja = random.nextInt(listaPiosenek.size());
                } while (nowaPozycja == aktualnaPozycja); // Upewnij się, że to inna piosenka
                aktualnaPozycja = nowaPozycja;
            } else {
                aktualnaPozycja = 0; // Jeśli tylko jedna piosenka, to zawsze ona
            }

        } else {
            // W trybie normalnym, przejdź do następnej, zapętlając na końcu listy
            aktualnaPozycja = (aktualnaPozycja + 1) % listaPiosenek.size();
        }
        zmienPiosenke(); // Załaduj i odtwórz nową piosenkę
    }

    // Przechodzi do poprzedniej piosenki
    private void poprzedniaPiosenka() {
        if (listaPiosenek == null || listaPiosenek.isEmpty()) return; // Zabezpieczenie
        if (czyShuffle) {
            // Podobnie jak w nastepnaPiosenka dla trybu shuffle
            int nowaPozycja;
             if (listaPiosenek.size() > 1) {
                do {
                    nowaPozycja = random.nextInt(listaPiosenek.size());
                } while (nowaPozycja == aktualnaPozycja);
                aktualnaPozycja = nowaPozycja;
            } else {
                aktualnaPozycja = 0;
            }
        } else {
            // W trybie normalnym, przejdź do poprzedniej, zapętlając na początku listy
            aktualnaPozycja = (aktualnaPozycja - 1 + listaPiosenek.size()) % listaPiosenek.size();
        }
        zmienPiosenke(); // Załaduj i odtwórz nową piosenkę
    }

    // Wspólna metoda do zmiany piosenki (wywoływana przez nastepnaPiosenka i poprzedniaPiosenka)
    private void zmienPiosenke() {
        if (listaPiosenek == null || listaPiosenek.isEmpty() || aktualnaPozycja < 0 || aktualnaPozycja >= listaPiosenek.size()) {
            // Dodatkowe zabezpieczenie przed wyjściem poza zakres, jeśli coś poszło nie tak z logiką shuffle/next/prev
            return;
        }
        aktualnaPiosenka = listaPiosenek.get(aktualnaPozycja); // Pobierz nowy obiekt Piosenka
        ustawDanePiosenki(); // Zaktualizuj UI z danymi nowej piosenki
        ustawRozmyteTlo(); // Zaktualizuj tło i kolory
        inicjalizujMediaPlayer(); // Zainicjuj MediaPlayer dla nowej piosenki
    }

    // Formatuje czas z milisekund do formatu MM:SS
    private String formatujCzas(int milisekundy) {
        int sekundy = (milisekundy / 1000) % 60;
        int minuty = (milisekundy / (1000 * 60)) % 60; // Powinno być (milisekundy / (1000 * 60)) bez % 60, jeśli chcemy minuty > 59
                                                    // Ale dla piosenek zwykle jest to OK.
                                                    // Dla poprawności dla dłuższych audio: int godziny = milisekundy / (1000 * 60 * 60);
        return String.format("%02d:%02d", minuty, sekundy); // %02d oznacza liczbę całkowitą, minimum 2 cyfry, z zerem wiodącym
    }

    // Metoda cyklu życia aktywności, wywoływana tuż przed zniszczeniem aktywności
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Bardzo ważne: zwolnienie zasobów MediaPlayer, aby uniknąć wycieków
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null); // Usunięcie wszystkich zaplanowanych zadań z handlera, aby uniknąć wycieków
    }
}