package com.lssd.cad_erp.core.identity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission_groups")
@Getter
@Setter
public class PermissionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "group_type")
    private String type; // e.g., 'DEPARTMENT', 'SPECIAL'

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "group_permissions",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}