package com.yer.library.service;

import com.yer.library.model.Customer;
import com.yer.library.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.of;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements CrudService<Customer> {
    private final CustomerRepository customerRepository;

    @Override
    public Customer get(Long customerId) {
        log.info("Fetching customer with id: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new IllegalStateException(
                        "customer with id " + customerId + " does not exist"
                )
        );
        if (customer.getDeleted()) {
            throw new IllegalStateException("customer with id " + customer.getId() + " does not exist");
        }
        return customer;
    }

    @Override
    public Collection<Customer> list(int limit) {
        log.info("Listing all customers");
        return customerRepository.listAvailable(of(0, limit));
    }

    @Override
    public Customer add(Customer customer) {
        log.info("Adding new customer (name = {})", customer.getName());
        customerRepository.findByEmail(customer.getEmailAddress()).ifPresent(existingCustomer -> {
            if (existingCustomer.getDeleted()) {
                customer.setId(existingCustomer.getId());
            } else {
                throw new IllegalStateException("email " + customer.getEmailAddress() + " already exists.");
            }
        });
        return customerRepository.save(customer);
    }

    @Override
    public Customer fullUpdate(Long customerId, Customer updatedCustomer) {
        log.info("Updating customer with id: {}", customerId);
        Customer existingCustomer = customerRepository.findById(customerId).orElseThrow(
                () -> new IllegalStateException(
                        "customer with id " + customerId + " does not exist"
                )
        );

        if (!updatedCustomer.getEmailAddress().equals(existingCustomer.getEmailAddress())) {
            customerRepository.findByEmail(updatedCustomer.getEmailAddress()).ifPresent(customerWithSameEmail -> {
                if (!customerWithSameEmail.getDeleted()) {
                    throw new IllegalStateException("email " + customerWithSameEmail.getEmailAddress() + " already exists");
                }
            });
        }

        updatedCustomer.setId(customerId);
        customerRepository.save(updatedCustomer);

        return updatedCustomer;
    }

    @Override
    public Boolean delete(Long customerId) {
        log.info("Deleting customer with id: {}", customerId);

        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new IllegalStateException(
                        "customer with id " + customerId + " does not exist"
                )
        );

        if (customer.getDeleted()) {
            throw new IllegalStateException(
                    "customer with id " + customerId + " has already been deleted"
            );
        }
        customer.setDeleted(true);

        return TRUE;
    }
}
