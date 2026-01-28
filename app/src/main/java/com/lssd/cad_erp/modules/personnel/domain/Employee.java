package com.lssd.cad_erp.modules.personnel.domain;

import com.lssd.cad_erp.core.identity.domain.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "badge_number")
    private String badgeNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_id")
    private Rank rank;

    public String getFullName() {
        if (firstName == null && lastName == null) return "Unknown Employee";
        return firstName + " " + lastName;
    }
}