package com.github.youssefwadie.readwithmedataloader.bootstrap;

import com.github.youssefwadie.readwithmedataloader.author.AuthorRepository;
import com.github.youssefwadie.readwithmedataloader.book.Book;
import com.github.youssefwadie.readwithmedataloader.book.BookRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


@RequiredArgsConstructor
public class WorkLoader implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Value("${datadump.location.work}")
    private String workDumpLocation;
    private Path workDumpPath;

    @PostConstruct
    public void init() throws URISyntaxException {
        final URL workDumpURL = AuthorLoader.class.getClassLoader().getResource(workDumpLocation);
        workDumpPath = Paths.get(workDumpURL.toURI());
    }

    @Override
    public void run(String... args) throws Exception {
        initWorks();
    }

    private void initWorks() {
        try (final Stream<String> lines = Files.lines(workDumpPath)) {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            lines.forEach(line -> {
                // Read and parse the line
                final String jsonString = line.substring(line.indexOf("{"));
                try {
                    final JSONObject jsonObject = new JSONObject(jsonString);
                    // Construct the Work object
                    val book = new Book();
                    book.setId(jsonObject.optString("key").replace("/works/", ""));

                    book.setName(jsonObject.optString("title"));

                    book.setDescription(parseDescription(jsonObject));
                    book.setPublishDate(parsePublishDate(jsonObject, dateTimeFormatter));
                    book.setCoverIds(parseCoverIds(jsonObject));
                    val authorIds = parseAuthorIds(jsonObject);
                    book.setAuthorIds(authorIds);
                    book.setAuthorNames(getAuthorNames(authorIds));

                    // Persist the author with the BookRepository
                    System.out.println("Saving book " + book.getName() + "...");
                    bookRepository.save(book);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private List<String> getAuthorNames(List<String> authorIds) {
        return authorIds
                .stream()
                .map(authorRepository::findById)
                .map(optionalAuthor -> {
                    if (optionalAuthor.isEmpty()) return "Unknown author";
                    return optionalAuthor.get().getName();
                })
                .toList();

    }

    private List<String> parseAuthorIds(JSONObject jsonObject) throws JSONException {
        val authorsJsonArray = jsonObject.optJSONArray("authors");
        if (authorsJsonArray == null) return Collections.emptyList();

        val authorIds = new ArrayList<String>();
        for (int i = 0; i < authorsJsonArray.length(); i++) {
            val authorId = authorsJsonArray.getJSONObject(i)
                    .getJSONObject("author")
                    .getString("key").replace("/authors/", "");
            authorIds.add(authorId);
        }
        return authorIds;
    }

    private List<String> parseCoverIds(JSONObject jsonObject) throws JSONException {
        val coversJsonArray = jsonObject.optJSONArray("covers");
        if (coversJsonArray == null) return Collections.emptyList();

        val coverIds = new ArrayList<String>();
        for (int i = 0; i < coversJsonArray.length(); i++) {
            coverIds.add(coversJsonArray.getString(i));
        }
        return coverIds;
    }

    private LocalDate parsePublishDate(JSONObject jsonObject, DateTimeFormatter dateTimeFormatter) {
        val publishedObject = jsonObject.optJSONObject("created");
        if (publishedObject == null) return null;
        String dateStr = publishedObject.optString("value");
        return LocalDate.parse(dateStr, dateTimeFormatter);
    }

    private String parseDescription(JSONObject jsonObject) {
        val descriptionObject = jsonObject.optJSONObject("description");
        if (descriptionObject == null) {
            return "";
        }
        return descriptionObject.optString("value");
    }
}
