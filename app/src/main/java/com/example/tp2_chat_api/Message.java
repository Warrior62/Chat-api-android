package com.example.tp2_chat_api;

public class Message {
    String id;
    String contenu;
    String auteur;
    String couleur;


    // {"id":"23","contenu":"0","auteur":"test","couleur":"black"


    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", contenu='" + contenu + '\'' +
                ", auteur='" + auteur + '\'' +
                ", couleur='" + couleur + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getCouleur() {
        return couleur;
    }
}
