package com.example.soundhive;

import java.io.Serializable;

public class Piosenka implements Serializable {
    private String tytul;
    private String album;
    private String wykonawca;
    private int okładka;
    private int plikAudio;

    public Piosenka(String tytul, String album, String wykonawca, int okładka, int plikAudio) {
        this.tytul = tytul;
        this.album = album;
        this.wykonawca = wykonawca;
        this.okładka = okładka;
        this.plikAudio = plikAudio;
    }

    public String getTytul() { return tytul; }
    public String getAlbum() { return album; }
    public String getWykonawca() { return wykonawca; }
    public int getOkładka() { return okładka; }
    public int getPlikAudio() { return plikAudio; }
}