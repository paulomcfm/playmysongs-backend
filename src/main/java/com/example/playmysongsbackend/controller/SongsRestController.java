package com.example.playmysongsbackend.controller;

import com.example.playmysongsbackend.entity.Song;
import com.example.playmysongsbackend.repository.SongsRepository;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping(value="/apis")
public class SongsRestController {
    @Autowired
    SongsRepository srepo;

    @GetMapping(value = "/teste")
    public ResponseEntity<Object> testarConexao(){
        return ResponseEntity.ok("Bem vindo a API de movies");
    }
    @GetMapping(value = "/get-musicas/{nome}")
    public ResponseEntity<Object> getMusicas(@PathVariable("nome") String nome){
        List<Song> resultados = new ArrayList<>();

        File pasta = new File("src/main/resources/static/musicas");
        for (File file : pasta.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getName();
                Pattern pattern = Pattern.compile(Pattern.quote(nome), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find() && fileName.endsWith(".mp3")) {
                    resultados.add(new Song(fileName));
                }
            }
        }
        return ResponseEntity.ok(resultados);
    }

    @PostMapping(value = "add-musica")
    public ResponseEntity<Object> addMusica(@RequestParam("nome") String nome,
                                          @RequestParam("artista") String artista,
                                          @RequestParam("genero") String genero,
                                          @RequestParam("musica") MultipartFile musica){
        nome=nome.replaceAll("\\s+", "");
        artista=artista.replaceAll("\\s+", "");
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        Matcher matcherNome = pattern.matcher(nome);
        Matcher matcherArtista = pattern.matcher(artista);
        if(matcherNome.matches() && matcherArtista.matches()) {
            String nomeArquivo = nome + "_" + genero + "_" + artista + ".mp3";
            srepo.addSong(new Song(nomeArquivo));
            Path root = Paths.get("src/main/resources/static/musicas");
            try {
                Files.copy(musica.getInputStream(),root.resolve(nomeArquivo));
            }catch (Exception e){
                return ResponseEntity.badRequest().body("Erro "+e.getMessage());
            }
            return ResponseEntity.ok("Ok, música adicionada");
        }
        return ResponseEntity.badRequest().body("Falha ao adicionar música.");
    }
}
