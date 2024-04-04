package com.example.playmysongsbackend.controller;

import com.example.playmysongsbackend.entity.Song;
import com.example.playmysongsbackend.repository.SongsRepository;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    public String getStaticPath() throws IOException {
        String staticPath = null;
        staticPath = resourceLoader.getResource("classpath:static").getFile().getAbsolutePath();
        return staticPath;
    }

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping(value = "/teste")
    public ResponseEntity<Object> testarConexao(){
        return ResponseEntity.ok("Bem vindo a API de movies");
    }
    @GetMapping(value = "/get-musicas/{nome}")
    public ResponseEntity<Object> getMusicas(@PathVariable("nome") String nome) throws IOException {
        List<Song> resultados = new ArrayList<>();
        String caminho = getStaticPath();
        File pasta = new File(caminho);
        for (File file : pasta.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getName();
                Pattern pattern = Pattern.compile("([^_]+)_([^_]+)_([^_]+)\\.mp3");
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.find()) {
                    String nomeArquivo = matcher.group(1);
                    String artista = matcher.group(3);
                    if (nomeArquivo.toLowerCase().contains(nome.toLowerCase()) || artista.toLowerCase().contains(nome.toLowerCase())) {
                        resultados.add(new Song(fileName,caminho));
                    }
                }
            }
        }
        return ResponseEntity.ok(resultados);
    }

    @PostMapping(value = "add-musica")
    public ResponseEntity<Object> addMusica(@RequestParam("nome") String nome,
                                          @RequestParam("artista") String artista,
                                          @RequestParam("genero") String genero,
                                          @RequestParam("musica") MultipartFile musica) throws IOException {
        nome=nome.replaceAll("\\s+", "");
        artista=artista.replaceAll("\\s+", "");
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        Matcher matcherNome = pattern.matcher(nome);
        Matcher matcherArtista = pattern.matcher(artista);
        if(matcherNome.matches() && matcherArtista.matches()) {
            String nomeArquivo = nome + "_" + genero + "_" + artista + ".mp3";
            srepo.addSong(new Song(nomeArquivo, getStaticPath()));
            try {
                Path root = Path.of(getStaticPath());
                Files.copy(musica.getInputStream(),root.resolve(nomeArquivo));
            }catch (Exception e){
                return ResponseEntity.badRequest().body("Erro "+e.getMessage());
            }
            return ResponseEntity.ok("Ok, música adicionada");
        }
        return ResponseEntity.badRequest().body("Falha ao adicionar música.");
    }
}
