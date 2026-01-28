package com.lssd.cad_erp.modules.personnel.domain;

import com.lssd.cad_erp.core.identity.domain.Permission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ranks")
@Getter
@Setter
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer weight = 0;

    @Column(length = 7)
    private String color = "#808080";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rank_permissions",
        joinColumns = @JoinColumn(name = "rank_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}