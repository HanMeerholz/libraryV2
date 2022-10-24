package com.yer.library.service;

import com.yer.library.model.Customer;
import com.yer.library.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService underTest;

    @Test
    void getExistingNonDeletedCustomer() {
        // given
        Long customerId = 1L;

        Customer customer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        customer.setId(customerId);
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));

        // when
        Customer returnedCustomer = underTest.get(customerId);

        // then
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );
        assertThat(returnedCustomer).isEqualTo(customer);
    }

    @Test
    void getExistingDeletedCustomer() {
        // given
        Long customerId = 1L;
        Customer customer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        customer.setId(customerId);
        customer.setDeleted(true);
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));

        // when
        assertThatThrownBy(() -> underTest.get(customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("customer with id " + customerId + " does not exist");
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );
        verify(customerRepository, never()).save(any());
    }

    @Test
    void getNonExistingCustomer() {
        // given
        Long customerId = 1L;

        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.get(customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("customer with id " + customerId + " does not exist");
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );
        verify(customerRepository, never()).save(any());
    }

    @Test
    void list() {
        // given
        int limit = 100;

        // when
        underTest.list(limit);

        // then
        verify(customerRepository).listAvailable(
                argThat(pageable -> pageable.equals(Pageable.ofSize(limit)))
        );
    }

    @Test
    void addValidCustomer() {
        // given
        Long customerId = 1L;

        Customer customer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        Customer expectedReturnedCustomer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        expectedReturnedCustomer.setId(customerId);
        given(customerRepository.save(customer)).willReturn(expectedReturnedCustomer);

        // when
        Customer returnedCustomer = underTest.add(customer);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).isEqualTo(customer);
        assertThat(returnedCustomer).isEqualTo(expectedReturnedCustomer);
    }

    @Test
    void addCustomerWithEmailTakenByExistingCustomer() {
        // given
        String email = "k.dickens@gmail.com";
        Customer customer1 = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25)
        );
        Customer customer2 = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                email,
                LocalDate.of(1998, Month.JUNE, 8)
        );

        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer1));

        // when
        // then
        assertThatThrownBy(() -> underTest.add(customer2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("email " + customer2.getEmailAddress() + " already exists");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void addCustomerWithEmailTakenByDeletedCustomer() {
        // given
        Long deletedCustomerInDBId = 1L;
        String email = "k.dickens@gmail.com";
        Customer deletedCustomerInDB = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25)
        );
        deletedCustomerInDB.setId(deletedCustomerInDBId);
        deletedCustomerInDB.setDeleted(true);

        Customer newCustomer = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                email,
                LocalDate.of(1998, Month.JUNE, 8)
        );
        Customer expectedReturnedCustomer = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                email,
                LocalDate.of(1998, Month.JUNE, 8)
        );
        expectedReturnedCustomer.setId(deletedCustomerInDBId);

        given(customerRepository.save(newCustomer)).willReturn(expectedReturnedCustomer);
        given(customerRepository.findByEmail(email)).willReturn(Optional.of(deletedCustomerInDB));

        // when
        Customer returnedCustomer = underTest.add(newCustomer);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).isEqualTo(expectedReturnedCustomer);
        assertThat(returnedCustomer).isEqualTo(expectedReturnedCustomer);
    }

    @Test
    void fullUpdateExistingCustomerSameEmail() {
        // given
        Long customerId = 1L;
        String email = "k.dickens@gmail.com";
        Customer initialCustomer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.APRIL, 25)
        );
        initialCustomer.setId(customerId);

        Customer updatedCustomer = new Customer(
                "Kaden Hickens",
                "834 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.MAY, 25)
        );

        Customer expectedReturnedCustomer = new Customer(
                "Kaden Hickens",
                "834 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.MAY, 25)
        );
        expectedReturnedCustomer.setId(customerId);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(initialCustomer));
        given(customerRepository.save(updatedCustomer)).willReturn(expectedReturnedCustomer);

        // when
        Customer returnedCustomer = underTest.fullUpdate(customerId, updatedCustomer);

        // then
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer).isEqualTo(updatedCustomer);
        assertThat(returnedCustomer).isEqualTo(expectedReturnedCustomer);
    }

    @Test
    void fullUpdateNonExistingCustomer() {
        // given
        Long customerId = 1L;
        String email = "k.dickens@gmail.com";

        Customer updatedCustomer = new Customer(
                "Kaden Hickens",
                "834 Vincenza Loaf",
                email,
                LocalDate.of(1953, Month.MAY, 25)
        );
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(customerId, updatedCustomer))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("customer with id " + customerId + " does not exist");
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );
        verify(customerRepository, never()).save(any());
    }

    @Test
    void fullUpdateExistingCustomerNewEmailExistsForNonDeletedCustomer() {
        // given
        Long customer1Id = 1L;
        Customer existingCustomer1 = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        existingCustomer1.setId(customer1Id);

        Long customer2Id = 2L;
        String newEmail = "iaincarter@hotmail.com";
        Customer existingCustomer2 = new Customer(
                "Iain Carter",
                "950 Poplar St.",
                newEmail,
                LocalDate.of(1998, Month.JUNE, 8)
        );
        existingCustomer2.setId(customer2Id);

        Customer updatedCustomer = new Customer(
                "Kaden Hickens",
                "834 Vincenza Loaf",
                newEmail,
                LocalDate.of(1953, Month.MAY, 25)
        );

        given(customerRepository.findById(customer1Id)).willReturn(Optional.of(existingCustomer1));
        given(customerRepository.findByEmail(newEmail)).willReturn(Optional.of(existingCustomer2));

        // when
        // then
        assertThatThrownBy(() -> underTest.fullUpdate(customer1Id, updatedCustomer))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("email " + newEmail + " already exists");
        verify(customerRepository).findById(
                argThat(id -> id.equals(customer1Id))
        );
        verify(customerRepository, never()).save(any());
    }

    @Test
    void deleteExistingNonDeletedCustomer() {
        // given
        Long customerId = 1L;
        Customer existingCustomer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        existingCustomer.setId(customerId);
        given(customerRepository.findById(customerId)).willReturn(Optional.of(existingCustomer));

        // when
        Boolean result = underTest.delete(customerId);

        // then
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );
        assertThat(result).isTrue();
        assertThat(existingCustomer.getDeleted()).isTrue();
    }

    @Test
    void deleteExistingDeletedCustomer() {
        // given
        Long customerId = 1L;
        Customer existingCustomer = new Customer(
                "Kaden Dickens",
                "835 Vincenza Loaf",
                "k.dickens@gmail.com",
                LocalDate.of(1953, Month.APRIL, 25)
        );
        existingCustomer.setDeleted(true);
        existingCustomer.setId(customerId);
        given(customerRepository.findById(customerId)).willReturn(Optional.of(existingCustomer));

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("customer with id " + customerId + " has already been deleted");
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );
    }

    @Test
    void deleteNonExistingCustomer() {
        // given
        Long customerId = 1L;
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.delete(customerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("customer with id " + customerId + " does not exist");
        verify(customerRepository).findById(
                argThat(id -> id.equals(customerId))
        );
    }
}