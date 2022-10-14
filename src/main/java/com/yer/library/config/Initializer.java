package com.yer.library.config;

import com.yer.library.model.Book;
import com.yer.library.model.BookCopy;
import com.yer.library.model.Customer;
import com.yer.library.model.Location;
import com.yer.library.repository.BookCopyRepository;
import com.yer.library.repository.BookRepository;
import com.yer.library.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class Initializer {

    @Bean
    CommandLineRunner commandLineRunner(BookRepository bookRepository, BookCopyRepository bookCopyRepository, CustomerRepository customerRepository) {
        return args -> {
            Book book1 = new Book(
                    "978-0123456789",
                    "Test",
                    Year.of(2017),
                    "John Doe",
                    "non-fiction",
                    null,
                    4000);

            Book book2 = new Book(
                    "978-0123456781",
                    "Test2",
                    Year.of(1970),
                    "John Doe II",
                    "fiction",
                    "fantasy",
                    5000);

            // Java 8 equivalent of ``List.of(test1, test2)''
            bookRepository.saveAll(Collections.unmodifiableList(Arrays.asList(book1, book2)));

            BookCopy bookCopy1 = new BookCopy(
                    book1, new Location((short) 1, (short) 1, (short) 1));
            BookCopy bookCopy2 = new BookCopy(
                    book2, new Location((short) 2, (short) 2, (short) 3));
            BookCopy bookCopy3 = new BookCopy(
                    book1, new Location((short) 2, (short) 1, (short) 1));
            BookCopy bookCopy4 = new BookCopy(
                    book1, new Location((short) 2, (short) 1, (short) 1));

            bookCopyRepository.saveAll(Collections.unmodifiableList(Arrays.asList(bookCopy1, bookCopy2, bookCopy3, bookCopy4)));

            Customer customer1 = new Customer(
                    "Amelia Bedelia",
                    "Sesame Street 123",
                    "amelia@bedelia.hotmail.com",
                    LocalDate.of(1990, 3, 4)
            );
            Customer customer2 = new Customer(
                    "John Doe",
                    "54th Street 203",
                    "johndoe@mail.com",
                    LocalDate.of(1970, 1, 1)
            );
            Customer customer3 = new Customer(
                    "Gerald",
                    "Roadsquare 16",
                    "notjohndoe@mail.com",
                    LocalDate.of(2002, 4, 11)
            );
            Customer customer4 = new Customer(
                    "Imposter",
                    null,
                    "anon@ymo.us",
                    LocalDate.of(2004, 11, 30)
            );

            customerRepository.saveAll(Collections.unmodifiableList(Arrays.asList(customer1, customer2, customer3, customer4)));
        };

    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token",
                "Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials", "Filename"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
