package com.example.akin.deneme.core;

import com.google.gson.Gson;

public final class GSON {
    private GSON() {}

    private static Gson gson = new Gson();

    public static <T> String toJson(T data) {
        return gson.toJson(data);
    }

    public static <T> T fromJson(String data, Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }
}
