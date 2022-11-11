package com.yer.library.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity(name = "User")
@Table(
        name = "users",
        uniqueConstraints = {                             // can't be unique because of soft delete
                @UniqueConstraint(
                        name = "username_unique",
                        columnNames = "username"
                )
        }
)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            columnDefinition = "VARCHAR(25)"
    )
    @NotBlank(message = "Username cannot be blank or null")
    @Size(max = 25, message = "Username cannot be longer than 25 symbols")
    private String username;

    @Column(
            name = "password",
            nullable = false,
            columnDefinition = "VARCHAR(100)"
    )
//    @Pattern(
//            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
//            message = "Password must be at least 8 symbols and contain at least one letter, one number, and one" +
//                    " special character"
//    )
    //@Size(min = 8, max = 25, message = "Password must between 8 and 25 characters")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
