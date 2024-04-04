package com.example.playmysongsbackend.entity;

public class Song {
    private String nomeArquivo;
    private String caminho;

    public Song(String nomeArquivo, String caminho) {
        this.nomeArquivo = nomeArquivo;
        this.caminho = caminho;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}
