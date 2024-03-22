package com.example.playmysongsbackend.repository;

import com.example.playmysongsbackend.entity.Song;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SongsRepository {
    private List<Song> songs;

    public SongsRepository() {
        this.songs = new ArrayList<>();
    }
    public List<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }
}
