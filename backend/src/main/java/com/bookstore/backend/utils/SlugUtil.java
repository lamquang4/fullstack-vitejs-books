package com.bookstore.backend.utils;

public class SlugUtil {

    public static String toSlug(String input) {
        if (input == null) return null;

        String slug = input.toLowerCase();

        slug = java.text.Normalizer.normalize(slug, java.text.Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        slug = slug.replaceAll("[^a-z0-9]+", "-");

        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }
}
