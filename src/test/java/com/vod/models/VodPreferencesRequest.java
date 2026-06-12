package com.vod.models;

import java.util.List;

public class VodPreferencesRequest {

    private List<Integer> genre_ids;
    private List<Integer> movie_ids;

    public VodPreferencesRequest(List<Integer> genre_ids, List<Integer> movie_ids) {
        this.genre_ids = genre_ids;
        this.movie_ids = movie_ids;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public List<Integer> getMovie_ids() {
        return movie_ids;
    }
}
