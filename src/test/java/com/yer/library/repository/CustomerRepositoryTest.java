package com.yer.library.repository;


import com.yer.library.model.Customer;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.PageRequest.ofSize;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByEmailThatExists() {
        // given
        String email = "k.dickens@gmail.com";
        Customer customer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25)
        );

        underTest.save(customer);

        // when
        Optional<Customer> actual = underTest.findByEmail(email);

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isNotNull();
        assertThat(actual.get().getEmailAddress()).isEqualTo(email);
    }

    @Test
    void findByEmailThatDoesNotExist() {
        // given
        String isbn = "978-2-3915-3957-4";

        // when
        Optional<Customer> actual = underTest.findByEmail(isbn);

        // then
        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    void listAvailableEmptyDatabase() {
        // when
        List<Customer> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void listAvailableOnlyDeletedCustomers() {
        Customer customer1 = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        customer1.setDeleted(true);
        Customer customer2 = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8)
        );
        customer2.setDeleted(true);

        List<Customer> customers = Collections.unmodifiableList(Arrays.asList(customer1, customer2));
        underTest.saveAll(customers);

        // when
        List<Customer> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(0);
    }

    @Test
    void listAvailableOneAvailableCustomer() {
        // given
        Customer customer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        underTest.save(customer);

        // when
        List<Customer> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(1).contains(customer);
    }

    @Test
    void listAvailableMultipleAvailableCustomers() {
        // given
        Customer customer1 = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        Customer customer2 = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8)
        );

        List<Customer> customers = Collections.unmodifiableList(Arrays.asList(customer1, customer2));
        underTest.saveAll(customers);

        // when
        List<Customer> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual).hasSize(2).contains(customer1).contains(customer2);
    }

    @Test
    void listAvailableMultipleAvailableAndDeletedCustomers() {
        // given
        Customer customer1 = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        customer1.setDeleted(true);
        Customer customer2 = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8)
        );
        Customer customer3 = new Customer(
                "Kylo Finch",
                null,
                "kylo.finch@yahoo.com",
                LocalDate.of(1943, Month.MAY, 7)
        );
        customer3.setDeleted(true);
        Customer customer4 = new Customer(
                "Henrietta Goodwin",
                "651 Santa Clara Street",
                "henrigoodwin@gmail.com",
                LocalDate.of(1961, Month.MARCH, 13)
        );

        List<Customer> customers = Collections.unmodifiableList(Arrays.asList(customer1, customer2, customer3, customer4));
        underTest.saveAll(customers);

        // when
        List<Customer> actual = underTest.listAvailable(ofSize(10));

        // then
        assertThat(actual)
                .hasSize(2)
                .doesNotContain(customer1)
                .contains(customer2)
                .doesNotContain(customer3)
                .contains(customer4);
    }

    @Test
    void listAvailablePageSizeSmallerThanTotalAmountOfCustomers() {
        // given
        Customer customer1 = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        Customer customer2 = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                "iaincarter@hotmail.com",
                LocalDate.of(1998, Month.JUNE, 8)
        );
        Customer customer3 = new Customer(
                "Kylo Finch",
                null,
                "kylo.finch@yahoo.com",
                LocalDate.of(1943, Month.MAY, 7)
        );
        Customer customer4 = new Customer(
                "Henrietta Goodwin",
                "651 Santa Clara Street",
                "henrigoodwin@gmail.com",
                LocalDate.of(1961, Month.MARCH, 13)
        );

        List<Customer> customers = Collections.unmodifiableList(Arrays.asList(customer1, customer2, customer3, customer4));
        underTest.saveAll(customers);

        // when
        List<Customer> actual = underTest.listAvailable(ofSize(3));

        // then
        // at least 3 customers of the list "customers" appear in actual
        assertThat(actual).hasSize(3).areAtLeast(3, new Condition<>(customers::contains, "containsNBooksOf"));
    }
}