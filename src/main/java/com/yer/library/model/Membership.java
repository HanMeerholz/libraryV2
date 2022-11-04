package com.yer.library.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity(name = "Membership")
@Table(name = "memberships")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Membership {
    public static final Period MAX_MEMBERSHIP_TIME = Period.ofYears(5);

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
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_memberships_membership_type")
    )
    @NotNull
    private MembershipType membershipType;

    @Column(
            name = "start_date"
    )
    @NotNull
    @PastOrPresent
    private LocalDate startDate;

    @Column(
            name = "end_date"
    )
    @NotNull
    private LocalDate endDate;

    @OneToMany(mappedBy = "membership")
    @JsonBackReference
    private Collection<Member> members = new ArrayList<>();

    @Column(
            name = "deleted"
    )
    private Boolean deleted = false;

    public Membership(MembershipType membershipType, LocalDate startDate, LocalDate endDate) {
        this.membershipType = membershipType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Membership that = (Membership) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "membershipType = " + membershipType + ", " +
                "startDate = " + startDate + ", " +
                "endDate = " + endDate + ", " +
                "deleted = " + deleted + ")";
    }

    @AssertTrue(message = "Field `startDate` should be later than `endDate`")
    private boolean isEndDateAfterStartDate() {
        if (startDate != null && endDate != null)
            return startDate.isBefore(endDate);
        return true;
    }

    @AssertTrue(message = "Field `startDate` must be at most 5 years before `endDate`")
    private boolean isValidMembershipTime() {
        if (startDate != null && endDate != null)
            return startDate.until(endDate).toTotalMonths() < MAX_MEMBERSHIP_TIME.toTotalMonths();
        return true;
    }
}
