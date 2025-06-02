package com.example.soundhive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterPiosenek.OnPiosenkaListener {



    private RecyclerView recyclerView;
    private AdapterPiosenek adapter;
    private ArrayList<Piosenka> listaPiosenek = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicjalizujListePiosenek();

        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        // Konfiguracja RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterPiosenek(this, listaPiosenek, this);
        recyclerView.setAdapter(adapter);
    }

    private void inicjalizujListePiosenek() {
        listaPiosenek.add(new Piosenka(
                "A Little Death",
                "I Love You",
                "The Neighbourhood",
                R.drawable.i_love_you,
                R.raw.a_little_death));

        listaPiosenek.add(new Piosenka(
                "Call It Fate, Call It Karma",
                "Comedown Machine",
                "The Strokes",
                R.drawable.comedown_machine,
                R.raw.call_it_fate));

        listaPiosenek.add(new Piosenka(
                "Taxi Cab",
                "Taxi Cab",
                "Twenty One Pilots",
                R.drawable.taxi_cab,
                R.raw.taxi_cab));

        listaPiosenek.add(new Piosenka(
                "Mr. Brightside",
                "Hot Fuss",
                "The Killers",
                R.drawable.hot_fuss,
                R.raw.mr_brightside));

        // Nowe propozycje
        listaPiosenek.add(new Piosenka(
                "Do I Wanna Know?",
                "AM",
                "Arctic Monkeys",
                R.drawable.am,
                R.raw.do_i_wanna_know));

        listaPiosenek.add(new Piosenka(
                "Take Me Out",
                "Franz Ferdinand",
                "Franz Ferdinand",
                R.drawable.franz_ferdinand,
                R.raw.take_me_out));

        listaPiosenek.add(new Piosenka(
                "Feel Good Inc.",
                "Demon Days",
                "Gorillaz",
                R.drawable.demon_days,
                R.raw.feel_good_inc));

        listaPiosenek.add(new Piosenka(
                "Last Nite",
                "Is This It",
                "The Strokes",
                R.drawable.is_this_it,
                R.raw.last_nite));

        listaPiosenek.add(new Piosenka(
                "Breezeblocks",
                "An Awesome Wave",
                "alt-J",
                R.drawable.an_awesome_wave,
                R.raw.breezeblocks));

        listaPiosenek.add(new Piosenka(
                "Seven Nation Army",
                "Elephant",
                "The White Stripes",
                R.drawable.elephant,
                R.raw.seven_nation_army));

        listaPiosenek.add(new Piosenka(
                "The Less I Know The Better",
                "Currents",
                "Tame Impala",
                R.drawable.currents,
                R.raw.the_less_i_know_the_better));

        listaPiosenek.add(new Piosenka(
                "Fluorescent Adolescent",
                "Favourite Worst Nightmare",
                "Arctic Monkeys",
                R.drawable.favourite_worst_nightmare,
                R.raw.fluorescent_adolescent));

        listaPiosenek.add(new Piosenka(
                "Lazy Eye",
                "Carnavas",
                "Silversun Pickups",
                R.drawable.carnavas,
                R.raw.lazy_eye));

        listaPiosenek.add(new Piosenka(
                "Pumped Up Kicks",
                "Torches",
                "Foster the People",
                R.drawable.torches,
                R.raw.pumped_up_kicks));

        listaPiosenek.add(new Piosenka(
                "Somebody Told Me",
                "Hot Fuss",
                "The Killers",
                R.drawable.hot_fuss,
                R.raw.somebody_told_me));

        listaPiosenek.add(new Piosenka(
                "What're We Doing Here",
                "What're We Doing Here",
                "ALEXSUCKS",
                R.drawable.whatwe,
                R.raw.what_we_doing_here));

        listaPiosenek.add(new Piosenka(
                "The Sink",
                "Maine",
                "hey, nothing",
                R.drawable.meine,
                R.raw.the_sink));

        listaPiosenek.add(new Piosenka(
                "505",
                "Favourite Worst Nightmare",
                "Arctic Monkeys",
                R.drawable.favourite_worst_nightmare,
                R.raw.fof));

        listaPiosenek.add(new Piosenka(
                "Shape Of My Heart",
                "Ten Summoner's Tales",
                "Sting",
                R.drawable.ten_summoners,
                R.raw.shape_of_my_heart));

        listaPiosenek.add(new Piosenka(
                "Let Down",
                "OK Computer",
                "Radiohead",
                R.drawable.ok_computer,
                R.raw.let_down));

        listaPiosenek.add(new Piosenka(
                "Hot N Cold",
                "Hot N Cold",
                "Katy Perry",
                R.drawable.hotncold,
                R.raw.hot_n_cold));

        listaPiosenek.add(new Piosenka(
                "Sparks",
                "Parachutes",
                "Coldplay",
                R.drawable.parachutes,
                R.raw.sparks));

        listaPiosenek.add(new Piosenka(
                "Every Breath You Take",
                "Synchronicity",
                "The Police",
                R.drawable.synchronicity,
                R.raw.every_breath));

        listaPiosenek.add(new Piosenka(
                "Everybody Wants To Rule The World",
                "Songs From The Big Chair",
                "Tears For Fears",
                R.drawable.songs_from,
                R.raw.everybody_wants));

        listaPiosenek.add(new Piosenka(
                "Boys Don't Cry",
                "Three Imaginary Boys",
                "The Cure",
                R.drawable.three_imaginary,
                R.raw.boys_dont_cry));

        listaPiosenek.add(new Piosenka(
                "I Kissed a Girl",
                "Hot N Cold",
                "Katy Perry",
                R.drawable.hotncold,
                R.raw.i_kiss_a_girl));


        listaPiosenek.add(new Piosenka(
                "Meet Me Halfway",
                "THE E.N.D. (THE ENERGY NEVER DIES)",
                "The Black Eyed Peas",
                R.drawable.end,
                R.raw.meet_me));


        listaPiosenek.add(new Piosenka(
                "Animal I Have Become",
                "One-X",
                "Three Days Grace",
                R.drawable.onex,
                R.raw.animal));

        listaPiosenek.add(new Piosenka(
                "Come As You Are",
                "Nevermind",
                "Nirvana",
                R.drawable.nevermind,
                R.raw.come_as_you_are));

        listaPiosenek.add(new Piosenka(
                "Creep",
                "Pablo Honey",
                "Radiohead",
                R.drawable.pablo,
                R.raw.creep));


    }

    @Override
    public void onPiosenkaClick(int pozycja) {
        Intent intent = new Intent(this, ActivityOdtwarzania.class);
        intent.putExtra("piosenka", listaPiosenek.get(pozycja));
        intent.putExtra("listaPiosenek", listaPiosenek);
        intent.putExtra("pozycja", pozycja);
        startActivity(intent);
    }
}