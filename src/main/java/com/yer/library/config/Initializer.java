package com.yer.library.config;

import com.yer.library.model.*;
import com.yer.library.repository.BookCopyRepository;
import com.yer.library.repository.BookRepository;
import com.yer.library.repository.MembershipRepository;
import com.yer.library.repository.MembershipTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class Initializer {

    @Bean
    CommandLineRunner commandLineRunner(
            BookRepository bookRepository,
            BookCopyRepository bookCopyRepository,
            MembershipTypeRepository membershipTypeRepository,
            MembershipRepository membershipRepository) {
        return args -> {
            Book book1 = new Book(
                    "978-2-3915-3957-4",
                    "The Girl in the Veil",
                    Year.of(1948),
                    "Cole Lyons",
                    "fiction",
                    "horror",
                    4200
            );
            Book book2 = new Book(
                    "978-0-1011-1658-9",
                    "Legacy Circling",
                    Year.of(2001),
                    "Arla Salgado",
                    "fiction",
                    "romantic drama",
                    4200
            );
            Book book3 = new Book(
                    "978-0-6967-9461-2",
                    "Case of the Laughing Baboon",
                    Year.of(1945),
                    "Murat McCartney",
                    "fiction",
                    "fairy tale",
                    4200
            );
            Book book4 = new Book(
                    "978-6-3073-8763-1",
                    "The Serpent in the Stars",
                    Year.of(1995),
                    "Tyra Daniels",
                    "nonfiction",
                    "psychology",
                    4200
            );
            Book book5 = new Book(
                    "978-2-4584-9673-4",
                    "Crown of Truth",
                    Year.of(1909),
                    "Coral Truong",
                    "fiction",
                    "historical",
                    4200
            );

            // Java 8 equivalent of ``List.of(test1, test2)''
            bookRepository.saveAll(Collections.unmodifiableList(Arrays.asList(book1, book2, book3, book4, book5)));

            BookCopy bookCopy1 = new BookCopy(
                    book1, new Location((short) 3, (short) 21, (short) 3));
            BookCopy bookCopy2 = new BookCopy(
                    book2, new Location((short) 1, (short) 30, (short) 5));
            BookCopy bookCopy3 = new BookCopy(
                    book2, new Location((short) 2, (short) 13, (short) 6));
            BookCopy bookCopy4 = new BookCopy(
                    book2, new Location((short) 1, (short) 29, (short) 2));
            BookCopy bookCopy5 = new BookCopy(
                    book2, new Location((short) 1, (short) 29, (short) 2));
            BookCopy bookCopy6 = new BookCopy(
                    book3, new Location((short) 2, (short) 17, (short) 3));
            BookCopy bookCopy7 = new BookCopy(
                    book3, new Location((short) 3, (short) 1, (short) 1));
            BookCopy bookCopy8 = new BookCopy(
                    book5, new Location((short) 1, (short) 21, (short) 4));

            bookCopyRepository.saveAll(Collections.unmodifiableList(Arrays.asList(
                    bookCopy1, bookCopy2, bookCopy3, bookCopy4, bookCopy5, bookCopy6, bookCopy7, bookCopy8)));


            MembershipType childMembershipType = new MembershipType(MembershipTypeName.CHILD, 0);
            MembershipType adultMembershipType = new MembershipType(MembershipTypeName.ADULT, 500);
            MembershipType familyMembershipType = new MembershipType(MembershipTypeName.FAMILY, 1500);

            membershipTypeRepository.saveAll(Collections.unmodifiableList(Arrays.asList(
                    childMembershipType, adultMembershipType, familyMembershipType
            )));

            Membership membership1 = new Membership(
                    familyMembershipType,
                    LocalDate.of(2019, Month.MARCH, 3),
                    LocalDate.of(2021, Month.MARCH, 3));
            Membership membership2 = new Membership(
                    childMembershipType,
                    LocalDate.of(2019, Month.MAY, 20),
                    LocalDate.of(2022, Month.MAY, 20));
            Membership membership3 = new Membership(
                    adultMembershipType,
                    LocalDate.of(2020, Month.JUNE, 2),
                    LocalDate.of(2020, Month.JULY, 2));
            Membership membership4 = new Membership(
                    adultMembershipType,
                    LocalDate.of(2020, Month.SEPTEMBER, 3),
                    LocalDate.of(2022, Month.SEPTEMBER, 3));
            Membership membership5 = new Membership(
                    childMembershipType,
                    LocalDate.of(2020, Month.NOVEMBER, 3),
                    LocalDate.of(2021, Month.FEBRUARY, 3));
            Membership membership6 = new Membership(
                    childMembershipType,
                    LocalDate.of(2021, Month.AUGUST, 12),
                    LocalDate.of(2022, Month.AUGUST, 12));
            Membership membership7 = new Membership(
                    familyMembershipType,
                    LocalDate.of(2022, Month.MARCH, 1),
                    LocalDate.of(2022, Month.APRIL, 1));
            Membership membership8 = new Membership(
                    childMembershipType,
                    LocalDate.of(2022, Month.AUGUST, 31),
                    LocalDate.of(2024, Month.AUGUST, 31));
            Membership membership9 = new Membership(
                    adultMembershipType,
                    LocalDate.of(2022, Month.SEPTEMBER, 13),
                    LocalDate.of(2024, Month.SEPTEMBER, 13));

            membershipRepository.saveAll(Collections.unmodifiableList(Arrays.asList(
                    membership1, membership2, membership3, membership4, membership5, membership6, membership7, membership8, membership9)));

//            Member member1 = new Member(
//                    "Kaden Dickens",
//                    "835 Vincenza Loaf",
//                    "k.dickens@gmail.com",
//                    LocalDate.of(1953, Month.APRIL, 25)
//            );
//            Member member2 = new Member(
//                    "Iain Carter",
//                    "950 Poplar St.",
//                    "iaincarter@hotmail.com",
//                    LocalDate.of(1998, Month.JUNE, 8)
//            );
//            Member member3 = new Member(
//                    "Harry Carter",
//                    "950 Poplar St.",
//                    "harrycarter@hotmail.com",
//                    LocalDate.of(1996, Month.JULY, 4)
//            );
//            Member member4 = new Member(
//                    "Kylo Finch",
//                    null,
//                    "kylo.finch@yahoo.com",
//                    LocalDate.of(1943, Month.MAY, 7)
//            );
//            Member member5 = new Member(
//                    "Henrietta Goodwin",
//                    "651 Santa Clara Street",
//                    "henrigoodwin@gmail.com",
//                    LocalDate.of(1961, Month.MARCH, 13)
//            );
//            Member member6 = new Member(
//                    "Roza Cunningham",
//                    "618 East Ketch Harbour St.",
//                    "rozach@outlook.com",
//                    LocalDate.of(1974, Month.MAY, 1)
//            );
//            Member member7 = new Member(
//                    "Berend Cunningham",
//                    "618 East Ketch Harbour St.",
//                    "b.cunningham@hotmail.co.uk",
//                    LocalDate.of(1972, Month.JANUARY, 24)
//            );
//            Member member8 = new Member(
//                    "Sabiha Rawlings",
//                    "3615 Edsel Road",
//                    "sabiharawlings@gmail.com",
//                    LocalDate.of(2008, Month.DECEMBER, 5)
//            );
//
//            customerRepository.saveAll(Collections.unmodifiableList(Arrays.asList(
//                    member1, member2, member3, member4, member5, member6, member7, member8)));
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
