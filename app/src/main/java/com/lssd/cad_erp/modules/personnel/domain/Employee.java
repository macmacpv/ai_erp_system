package com.lssd.cad_erp.modules.personnel.domain;

import com.lssd.cad_erp.core.identity.domain.Account;
import com.lssd.cad_erp.core.identity.domain.PermissionGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "employee_groups",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<PermissionGroup> groups = new HashSet<>();

    public String getFullName() {
        if (firstName == null && lastName == null) return "Unknown Employee";
        return firstName + " " + lastName;
    }
}