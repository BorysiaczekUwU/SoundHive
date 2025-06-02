package com.example.soundhive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.graphics.drawable.GradientDrawable;
import android.content.Intent;
import android.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ActivityOdtwarzania extends AppCompatActivity {

    private ImageView obrazOkładka, tloImageView;
    private TextView tekstTytul, tekstAlbum, tekstWykonawca, tekstCzasAktualny, tekstCzasKoncowy;
    private SeekBar seekBar;
    private ImageView przyciskPlayPause, przyciskPrev, przyciskNext;
    private ToggleButton przyciskRepeat, przyciskShuffle;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Piosenka aktualnaPiosenka;
    private ArrayList<Piosenka> listaPiosenek;
    private ArrayList<Piosenka> oryginalnaListaPiosenek;
    private int aktualnaPozycja;
    private int kolorDominujacy;
    private boolean czyOdtwarzanie = true;
    private boolean czyRepeat = false;
    private boolean czyShuffle = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odtwarzania);

        inicjalizujWidoki();

        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        Intent intent = getIntent();
        aktualnaPiosenka = (Piosenka) intent.getSerializableExtra("piosenka");
        listaPiosenek = (ArrayList<Piosenka>) intent.getSerializableExtra("listaPiosenek");
        oryginalnaListaPiosenek = new ArrayList<>(listaPiosenek);
        aktualnaPozycja = intent.getIntExtra("pozycja", 0);

        ustawDanePiosenki();
        ustawRozmyteTlo();
        inicjalizujMediaPlayer();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
                tekstCzasAktualny.setText(formatujCzas(progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ActivityOdtwarzania.this.runOnUiThread(new Runnable() {
            @Override public void run() {
                if (mediaPlayer != null) {
                    int aktualnaPozycja = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(aktualnaPozycja);
                    tekstCzasAktualny.setText(formatujCzas(aktualnaPozycja));
                }
                handler.postDelayed(this, 1000);
            }
        });

        przyciskPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                przyciskPlayPause.setImageResource(R.drawable.play);
                czyOdtwarzanie = false;
            } else {
                mediaPlayer.start();
                przyciskPlayPause.setImageResource(R.drawable.pause);
                czyOdtwarzanie = true;
            }
        });

        przyciskNext.setOnClickListener(v -> nastepnaPiosenka());
        przyciskPrev.setOnClickListener(v -> poprzedniaPiosenka());

        przyciskRepeat.setOnClickListener(v -> {
            czyRepeat = przyciskRepeat.isChecked();
            if (czyRepeat) {
                mediaPlayer.setLooping(true);
                przyciskRepeat.setBackgroundResource(R.drawable.replay);
            } else {
                mediaPlayer.setLooping(false);
                przyciskRepeat.setBackgroundResource(R.drawable.foreward);
            }
        });

        przyciskShuffle.setOnClickListener(v -> {
            czyShuffle = przyciskShuffle.isChecked();
            if (czyShuffle) {
                Collections.shuffle(listaPiosenek);
                Piosenka aktualna = listaPiosenek.remove(aktualnaPozycja);
                listaPiosenek.add(0, aktualna);
                aktualnaPozycja = 0;
                przyciskShuffle.setBackgroundResource(R.drawable.shuffle);
            } else {
                listaPiosenek = new ArrayList<>(oryginalnaListaPiosenek);
                aktualnaPozycja = listaPiosenek.indexOf(aktualnaPiosenka);
                przyciskShuffle.setBackgroundResource(R.drawable.order
                );
            }
        });
    }

    private void inicjalizujWidoki() {
        obrazOkładka = findViewById(R.id.okladkaDuzaImageView);
        tloImageView = findViewById(R.id.tloImageView);
        tekstTytul = findViewById(R.id.tytulDuzyTextView);
        tekstAlbum = findViewById(R.id.albumTextView);
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

    private void ustawRozmyteTlo() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), aktualnaPiosenka.getOkładka());

        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        tloImageView.setImageDrawable(drawable);
        tloImageView.setRenderEffect(RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.CLAMP));
        Palette.from(bitmap).generate(palette -> {
            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
            if (dominantSwatch != null) {
                kolorDominujacy = dominantSwatch.getRgb();
                ustawKoloryInterfejsu();
            }
        });
    }

    private void ustawKoloryInterfejsu() {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.argb(200, Color.red(kolorDominujacy),
                        Color.green(kolorDominujacy),
                        Color.blue(kolorDominujacy)),
                        0xFF000000});
        findViewById(R.id.activityOdtwarzaniaLayout).setBackground(gradientDrawable);

        seekBar.getProgressDrawable().setColorFilter(kolorDominujacy, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(kolorDominujacy, PorterDuff.Mode.SRC_IN);

        przyciskPlayPause.setColorFilter(kolorDominujacy);

    }

    private void inicjalizujMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, aktualnaPiosenka.getPlikAudio());
        mediaPlayer.setLooping(czyRepeat);
        mediaPlayer.setOnCompletionListener(mp -> {
            if (!czyRepeat) {
                nastepnaPiosenka();
            }
        });
        tekstCzasKoncowy.setText(formatujCzas(mediaPlayer.getDuration()));
        mediaPlayer.start();
        przyciskPlayPause.setImageResource(R.drawable.pause);
        czyOdtwarzanie = true;
    }

    private void ustawDanePiosenki() {
        obrazOkładka.setImageResource(aktualnaPiosenka.getOkładka());
        tekstTytul.setText(aktualnaPiosenka.getTytul());
        tekstAlbum.setText(aktualnaPiosenka.getAlbum());
        tekstWykonawca.setText(aktualnaPiosenka.getWykonawca());
    }

    private void nastepnaPiosenka() {
        if (czyShuffle) {
            aktualnaPozycja = random.nextInt(listaPiosenek.size());
        } else {
            aktualnaPozycja = (aktualnaPozycja + 1) % listaPiosenek.size();
        }
        zmienPiosenke();
    }

    private void poprzedniaPiosenka() {
        if (czyShuffle) {
            aktualnaPozycja = random.nextInt(listaPiosenek.size());
        } else {
            aktualnaPozycja = (aktualnaPozycja - 1 + listaPiosenek.size()) % listaPiosenek.size();
        }
        zmienPiosenke();
    }

    private void zmienPiosenke() {
        aktualnaPiosenka = listaPiosenek.get(aktualnaPozycja);
        ustawDanePiosenki();
        ustawRozmyteTlo(); // Zmieniono z ustawKolorTla()
        inicjalizujMediaPlayer();
    }

    private String formatujCzas(int milisekundy) {
        int sekundy = (milisekundy / 1000) % 60;
        int minuty = (milisekundy / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minuty, sekundy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}