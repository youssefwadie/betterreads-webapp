package com.github.youssefwadie.readwithmedataloader.bootstrap;

import com.github.youssefwadie.readwithmedataloader.author.AuthorRepository;
import com.github.youssefwadie.readwithmedataloader.book.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapConfig {
//    @Bean
//    @Order(1001)
//    @Autowired
    public CommandLineRunner authorLoader(final AuthorRepository authorRepository) {
        return new AuthorLoader(authorRepository);
    }

    //    @Bean
//    @Order(1002)
//    @Autowired
    public CommandLineRunner workLoader(AuthorRepository authorRepository, BookRepository bookRepository) {
        return new WorkLoader(authorRepository, bookRepository);
    }

}
