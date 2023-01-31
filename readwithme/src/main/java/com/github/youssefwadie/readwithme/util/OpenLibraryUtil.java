package com.github.youssefwadie.readwithme.util;

import lombok.NonNull;
import lombok.val;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Simple utility class that provides a unified way to interact
 * with Open Library API
 */
public final class OpenLibraryUtil {
    private static final String COVER_IMAGES_ROOT_URL = "https://covers.openlibrary.org/b/id";

    private OpenLibraryUtil() {
    }

    /**
     * Gets the first cover url for the given coverIds, falls back to no-image if non found
     * @param coverIds list of coverIds
     * @param size cover size
     * @return the first cover url for the given coverIds
     */
    @NonNull
    public static String coverUrl(List<String> coverIds, String size) {
        Assert.notNull(size, "coverSize cannot be null");

        val coverId = (coverIds == null || coverIds.isEmpty()) ? null : coverIds.get(0);

        return coverUrl(coverId, size);
    }

    /**
     * Gets the cover url for coverId, falls back to no-image if non found
     *
     * @param coverId the coverId
     * @param size    cover size
     * @return the url of the book's cover with the given size
     */
    @NonNull
    public static String coverUrl(String coverId, String size) {
        Assert.notNull(size, "coverSize cannot be null");

        var coverImageUrl = "/images/no-image.png";
        if (StringUtils.hasText(coverId)) {
            coverImageUrl = String.format("%s/%s-%s.jpg", COVER_IMAGES_ROOT_URL, coverId, size);
        }
        return coverImageUrl;
    }
}
