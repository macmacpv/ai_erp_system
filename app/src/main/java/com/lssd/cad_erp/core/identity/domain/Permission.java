package com.lssd.cad_erp.core.identity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_string", unique = true, nullable = false)
    private String nodeString;

    public Permission() {}
    public Permission(String nodeString) { this.nodeString = nodeString; }
}