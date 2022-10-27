package com.yer.library.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "Membership")
@Table(name = "memberships")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Membership {
    @Id
    @SequenceGenerator(
            name = "membership_sequence",
            sequenceName = "membership_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "membership_sequence"
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "membership_type_id",
            foreignKey = @ForeignKey(name = "FK_memberships_membership_type")
    )
    private MembershipType membershipType;

    @Column(
            name = "start_date"
    )
    private LocalDate startDate;

    @Column(
            name = "end_date"
    )
    private LocalDate endDate;

    @Column(
            name = "deleted"
    )
    private Boolean deleted = false;

    public Membership(MembershipType membershipType, LocalDate startDate, LocalDate endDate) {
        this.membershipType = membershipType;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
