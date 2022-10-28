package com.yer.library.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "Member")
@Table(
        name = "members"
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
public class Member {
    @Id
    @SequenceGenerator(
            name = "member_sequence",
            sequenceName = "member_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "member_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

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

    @ManyToOne
    @JoinColumn(
            name = "membership_id",
            foreignKey = @ForeignKey(name = "FK_member_membership")
    )
    @JsonManagedReference
    private Membership membership;

    @Column(
            name = "deleted"
    )
    private Boolean deleted = false;

    public Member(String name, String homeAddress, String emailAddress, LocalDate birthday, Membership membership) {
        this.name = name;
        this.homeAddress = homeAddress;
        this.emailAddress = emailAddress;
        this.birthday = birthday;
        this.membership = membership;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Member member = (Member) o;
        return id != null && Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
