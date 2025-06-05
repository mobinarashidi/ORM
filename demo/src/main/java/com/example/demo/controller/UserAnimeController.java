package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.entity.Anime;
import com.example.demo.entity.UserAnime;
import com.example.demo.repository.UserAnimeRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController("")
public class UserAnimeController {

    private final UserAnimeRepository userAnimeRepository;

    public UserAnimeController(UserAnimeRepository userAnimeRepository) {
        this.userAnimeRepository = userAnimeRepository;
    }

    @GetMapping("/users/{username}/watched")
    public List<UserWatchedDTO> getWatchedAnimes(
            @PathVariable(name = "username") String username,
            @RequestParam(name = "count", defaultValue = "10") int count
    ) {
        return userAnimeRepository.findAll().stream()
                .filter(entry -> username.equals(entry.getUser().getUsername()))
                .sorted((e1, e2) -> {
                    int s1 = Integer.parseInt(e1.getMy_score());
                    int s2 = Integer.parseInt(e2.getMy_score());
                    return Integer.compare(s1, s2);
                })
                .limit(count)
                .map(entry -> new UserWatchedDTO(
                        entry.getAnime().getAnimeId(),
                        entry.getAnime().getTitle(),
                        entry.getMy_score(),
                        entry.getAnime().getEpisodes()
                ))
                .toList();
    }

    @GetMapping("/anime/popular")
    public List<GenreDTO> getPopularGenres() {
        Map<String, Long> genreCountMap = new HashMap<>();

        userAnimeRepository.findAll().stream()
                .map(UserAnime::getAnime)
                .map(Anime::getGenre)
                .flatMap(genreString -> Arrays.stream(genreString.split(",")))
                .map(String::trim)
                .filter(genre -> !genre.isEmpty())
                .forEach(genre -> {
                    genreCountMap.put(genre, genreCountMap.getOrDefault(genre, 0L) + 1);
                });

        return genreCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(e -> new GenreDTO(e.getKey(), e.getValue()))
                .toList();
    }


    @GetMapping("users/{username}/similars")
    public List<SimilarDTO> getSimilarUsers(@PathVariable(name = "username") String username) {
        Set<String> targetUserAnimeIds = userAnimeRepository.findAll().stream()
                .filter(entry -> username.equals(entry.getUser().getUsername()))
                .map(entry -> entry.getAnime().getAnimeId())
                .collect(Collectors.toSet());

        Map<String, Long> sharedCountMap = new HashMap<>();

        userAnimeRepository.findAll().stream()
                .filter(entry -> !entry.getUser().getUsername().equalsIgnoreCase(username))
                .filter(entry -> targetUserAnimeIds.contains(entry.getAnime().getAnimeId()))
                .forEach(entry -> {
                    String otherUsername = entry.getUser().getUsername();
                    sharedCountMap.put(otherUsername, sharedCountMap.getOrDefault(otherUsername, 0L) + 1);
                });

        return sharedCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .map(e -> new SimilarDTO(e.getKey(), e.getValue()))
                .toList();
    }
}
