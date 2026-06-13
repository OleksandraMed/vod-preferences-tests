package com.vod.models;

import java.util.List;

public record VodPreferencesRequest(List<Integer> genre_ids, List<Integer> movie_ids) {

}
