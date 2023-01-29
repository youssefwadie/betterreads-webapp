package com.github.youssefwadie.readwithmedataloader.bootstrap;

import com.github.youssefwadie.readwithmedataloader.author.Author;
import com.github.youssefwadie.readwithmedataloader.author.AuthorRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AuthorLoader implements CommandLineRunner {
    private final AuthorRepository authorRepository;

    @Value("${datadump.location.author}")
    private String authorDumpLocation;


    private Path authorDumpPath;

    @PostConstruct
    public void init() throws URISyntaxException {
        final URL authorDumpURL = AuthorLoader.class.getClassLoader().getResource(authorDumpLocation);
        authorDumpPath = Paths.get(authorDumpURL.toURI());
    }

    @Override
    public void run(String... args) throws Exception {
        initAuthors();
    }

    private void initAuthors() {
        try (final Stream<String> lines = Files.lines(authorDumpPath)) {
            lines.forEach(line -> {
                // Read and parse the line
                final String jsonString = line.substring(line.indexOf("{"));
                try {
                    final JSONObject jsonObject = new JSONObject(jsonString);
                    // Construct the Author object
                    final Author author = new Author();
                    author.setId(jsonObject.optString("key").replace("/authors/", ""));
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));

                    // Persist the author with the AuthorRepository
                    authorRepository.save(author);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
