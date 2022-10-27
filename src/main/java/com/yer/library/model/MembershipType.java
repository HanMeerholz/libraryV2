package com.yer.library.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "MembershipType")
@Table(name = "membership_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "membership_type_unique",
                        columnNames = "type"
                )
        })
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MembershipType {
    @Id
    @SequenceGenerator(
            name = "membership_type_sequence",
            sequenceName = "membership_type_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "membership_type_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false
    )
    MembershipTypeName type;

    @Column(
            name = "cost_per_month"
    )
    int costPerMonth;

    public MembershipType(MembershipTypeName type, int costPerMonth) {
        this.type = type;
        this.costPerMonth = costPerMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MembershipType that = (MembershipType) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
