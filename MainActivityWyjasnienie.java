package com.example.soundhive; // Deklaracja pakietu, w którym znajduje się klasa

// Importy niezbędnych klas z bibliotek Androida i Javy
import androidx.appcompat.app.AppCompatActivity; // Podstawowa klasa dla aktywności zapewniająca kompatybilność wsteczną
import androidx.recyclerview.widget.LinearLayoutManager; // Zarządza układem elementów w RecyclerView (jako lista pionowa/pozioma)
import androidx.recyclerview.widget.RecyclerView; // Komponent do wyświetlania dużych zbiorów danych w sposób wydajny (listy przewijane)
import android.content.Intent; // Służy do komunikacji między komponentami aplikacji (np. uruchamiania nowych aktywności)
import android.graphics.Color; // Klasa do obsługi kolorów
import android.os.Build; // Informacje o wersji systemu Android (tutaj nieużywane bezpośrednio, ale często potrzebne przy flagach okna)
import android.os.Bundle; // Służy do przekazywania danych między stanami aktywności (np. przy obrocie ekranu)
import android.view.View; // Podstawowa klasa dla wszystkich komponentów UI
import android.view.WindowManager; // Zarządza właściwościami okna aplikacji

import java.util.ArrayList; // Implementacja dynamicznej listy (tablicy)

// Deklaracja klasy MainActivity
// Dziedziczy po AppCompatActivity, co czyni ją standardową aktywnością
// Implementuje AdapterPiosenek.OnPiosenkaListener, co oznacza, że ta klasa będzie nasłuchiwać na zdarzenia kliknięcia elementów w RecyclerView
public class MainActivity extends AppCompatActivity implements AdapterPiosenek.OnPiosenkaListener {

    // Deklaracje pól klasy (zmiennych)
    private RecyclerView recyclerView; // Referencja do komponentu RecyclerView z layoutu XML
    private AdapterPiosenek adapter; // Adapter, który dostarcza dane do RecyclerView
    private ArrayList<Piosenka> listaPiosenek = new ArrayList<>(); // Lista obiektów Piosenka, która będzie źródłem danych dla adaptera

    // Metoda cyklu życia aktywności, wywoływana przy tworzeniu aktywności
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Wywołanie metody z klasy nadrzędnej (konieczne)
        setContentView(R.layout.activity_main); // Ustawienie layoutu dla tej aktywności (plik activity_main.xml)

        inicjalizujListePiosenek(); // Wywołanie metody inicjalizującej listę piosenek (dodaje dane do listaPiosenek)

        // --- Sekcja konfiguracji wyglądu okna (pasek nawigacji i statusu) ---
        // Celem tego bloku jest uczynienie paska nawigacyjnego (dolny) i paska statusu (górny)
        // przezroczystymi, aby layout aplikacji mógł się rozciągać pod nimi (tzw. edge-to-edge display).

        getWindow().setNavigationBarColor(Color.TRANSPARENT); // Ustawia kolor paska nawigacyjnego na przezroczysty
        getWindow().setStatusBarColor(Color.TRANSPARENT); // Ustawia kolor paska statusu na przezroczysty

        // Flagi te są potrzebne, aby przezroczystość działała poprawnie i aby system wiedział,
        // że aplikacja sama będzie rysować tła pod paskami systemowymi.
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); // Czyści flagę półprzezroczystej nawigacji (jeśli była ustawiona)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // Mówi systemowi, że okno aplikacji będzie rysować tło dla pasków systemowych

        // Ustawia flagi widoku systemowego, aby layout aplikacji rozciągał się
        // pod paskiem nawigacyjnym i zachowywał stabilność.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | // Layout będzie zajmował przestrzeń, jakby pasek nawigacyjny był ukryty
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Zapewnia stabilność layoutu przy zmianach widoczności UI systemowego
        );
        // --- Koniec sekcji konfiguracji wyglądu okna ---

        // --- Sekcja konfiguracji RecyclerView ---
        recyclerView = findViewById(R.id.recyclerView); // Znajduje RecyclerView w pliku layoutu (activity_main.xml) po jego ID
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Ustawia sposób ułożenia elementów w RecyclerView - jako pionową listę
                                                                    // 'this' to kontekst, czyli bieżąca aktywność

        // Tworzy nowy adapter, przekazując do niego:
        // 1. 'this' (kontekst)
        // 2. 'listaPiosenek' (dane do wyświetlenia)
        // 3. 'this' (MainActivity jako listener kliknięć, bo implementuje OnPiosenkaListener)
        adapter = new AdapterPiosenek(this, listaPiosenek, this);
        recyclerView.setAdapter(adapter); // Przypisuje adapter do RecyclerView, co powoduje wyświetlenie danych
        // --- Koniec sekcji konfiguracji RecyclerView ---
    }

    // Prywatna metoda do inicjalizacji (wypełnienia) listy piosenek
    // W tej wersji aplikacji lista jest "hardkodowana" - wpisana na stałe w kodzie.
    // W bardziej zaawansowanych aplikacjach dane pochodziłyby np. z bazy danych lub API.
    private void inicjalizujListePiosenek() {
        // Dodawanie nowych obiektów Piosenka do listy
        // Każdy obiekt Piosenka jest tworzony z:
        // - Tytułem (String)
        // - Nazwą albumu (String)
        // - Nazwą wykonawcy (String)
        // - Identyfikatorem zasobu graficznego (okładki) z folderu res/drawable (int)
        // - Identyfikatorem zasobu audio (pliku piosenki) z folderu res/raw (int)
        listaPiosenek.add(new Piosenka(
                "A Little Death",
                "I Love You",
                "The Neighbourhood",
                R.drawable.i_love_you, // Odwołanie do obrazka i_love_you.png/jpg itp. w res/drawable
                R.raw.a_little_death)); // Odwołanie do pliku a_little_death.mp3/wav itp. w res/raw

        // ... (kolejne dodawania piosenek, analogicznie) ...
        listaPiosenek.add(new Piosenka(
                "Call It Fate, Call It Karma",
                "Comedown Machine",
                "The Strokes",
                R.drawable.comedown_machine,
                R.raw.call_it_fate));

        // ... (i tak dalej dla wszystkich piosenek) ...

        listaPiosenek.add(new Piosenka(
                "Creep",
                "Pablo Honey",
                "Radiohead",
                R.drawable.pablo,
                R.raw.creep));
    }

    // Metoda wywoływana, gdy użytkownik kliknie na element listy (piosenkę)
    // Ta metoda jest częścią implementacji interfejsu AdapterPiosenek.OnPiosenkaListener
    @Override // Adnotacja wskazująca, że ta metoda nadpisuje metodę z interfejsu/klasy nadrzędnej
    public void onPiosenkaClick(int pozycja) {
        // 'pozycja' to indeks klikniętego elementu w 'listaPiosenek'

        // Tworzenie nowego Intentu, aby uruchomić ActivityOdtwarzania
        Intent intent = new Intent(this, ActivityOdtwarzania.class);
        // 'this' - kontekst bieżącej aktywności (MainActivity)
        // 'ActivityOdtwarzania.class' - klasa aktywności, którą chcemy uruchomić

        // Przekazywanie danych do ActivityOdtwarzania za pomocą "extras" w Intencie:
        // Aby to działało, klasa Piosenka musi implementować interfejs Parcelable lub Serializable.
        // Parcelable jest preferowane w Androidzie ze względu na wydajność.

        // Przekazuje obiekt Piosenka, który został kliknięty
        intent.putExtra("piosenka", listaPiosenek.get(pozycja));
        // "piosenka" - klucz, pod którym obiekt będzie dostępny w ActivityOdtwarzania
        // listaPiosenek.get(pozycja) - pobranie konkretnego obiektu Piosenka z listy

        // Przekazuje całą listę piosenek
        intent.putExtra("listaPiosenek", listaPiosenek);
        // "listaPiosenek" - klucz dla całej listy

        // Przekazuje pozycję (indeks) klikniętej piosenki w liście
        intent.putExtra("pozycja", pozycja);
        // "pozycja" - klucz dla indeksu

        startActivity(intent); // Uruchamia ActivityOdtwarzania z przygotowanym Intentem (i danymi)
    }
}