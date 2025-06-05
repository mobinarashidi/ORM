package com.example.demo.dto;

public class UpdateEpisodeDTO {
    private String animeId;
    private int episodes;

    public UpdateEpisodeDTO() {}

    public UpdateEpisodeDTO(String animeId, int episodes) {
        this.animeId = animeId;
        this.episodes = episodes;
    }

    public String getAnimeId() {
        return animeId;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setAnimeId(String animeId) {
        this.animeId = animeId;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }
}
