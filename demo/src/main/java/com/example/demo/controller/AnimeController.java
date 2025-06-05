package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Anime;
import com.example.demo.repository.AnimeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController("")
public class AnimeController {

    private final AnimeRepository animeRepository;

    public AnimeController(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;
    }


    @GetMapping("/anime/top")
    public List<TopAnimeDTO> getTop10AnimesByEpisodes() {
        return animeRepository.findAll().stream()
                .filter(anime -> anime.getEpisodes() != null)
                .sorted((a, b) -> Integer.compare(
                        Integer.parseInt(b.getEpisodes()),
                        Integer.parseInt(a.getEpisodes())
                ))
                .limit(10)
                .map(anime -> new TopAnimeDTO(
                        anime.getAnimeId(),
                        anime.getTitle(),
                        anime.getScore(),
                        anime.getEpisodes()
                ))
                .toList();
    }



    @PostMapping("/anime/{animeId}/episodes")
    public ResponseEntity<UpdateEpisodeDTO> updateAnimeEpisodes(
            @PathVariable(name = "animeId") String animeId,
            @RequestParam(name = "value", defaultValue = "1") int value
    ) {
        Optional<Anime> optionalAnime = animeRepository.findById(animeId);

        if (optionalAnime.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Anime anime = optionalAnime.get();
        int currentEpisodes = Integer.parseInt(anime.getEpisodes());
        int updatedEpisodes = currentEpisodes + value;
        anime.setEpisodes(String.valueOf(updatedEpisodes));
        animeRepository.save(anime);

        return ResponseEntity.ok(new UpdateEpisodeDTO(anime.getAnimeId(), updatedEpisodes));
    }

}