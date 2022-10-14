package com.yer.library.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "Customer")
@Table(
        name = "customers"
//        uniqueConstraints = {                             // can't be unique because of soft delete
//                @UniqueConstraint(
//                        name = "email_address_unique",
//                        columnNames = "email_address"
//                )
//        }
)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @SequenceGenerator(
            name = "customer_sequence",
            sequenceName = "customer_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "customer_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Column(
            name = "name",
            nullable = false,
            columnDefinition = "TEXT"
    )
    @NotEmpty(message = "Name cannot be empty or null")
    private String name;

    @Column(
            name = "home_address",
            columnDefinition = "TEXT"
    )
    private String homeAddress;

    @Column(
            name = "email_address",
            columnDefinition = "VARCHAR(100)"
    )
    @NotEmpty(message = "Email cannot be empty or null")
    private String emailAddress;

    @Column(
            name = "birthday",
            columnDefinition = "DATE"
    )
    private LocalDate birthday;

    @Column(
            name = "deleted"
    )
    private Boolean deleted = false;

    public Customer(String name, String homeAddress, String emailAddress, LocalDate birthday) {
        this.name = name;
        this.homeAddress = homeAddress;
        this.emailAddress = emailAddress;
        this.birthday = birthday;
    }
}
