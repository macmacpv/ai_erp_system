package com.lssd.cad_erp.core.identity.domain;

import com.lssd.cad_erp.modules.personnel.domain.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "is_root")
    private boolean root = false;

    @OneToOne(mappedBy = "account", fetch = FetchType.EAGER)
    private Employee employee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_permissions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> directPermissions = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (root) {
            return Set.of(new SimpleGrantedAuthority("ROLE_ROOT"), new SimpleGrantedAuthority("ROLE_USER"));
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        
        // 1. Direct Technical Permissions (from Account)
        authorities.addAll(directPermissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getNodeString()))
                .collect(Collectors.toSet()));

        // 2. Personnel Permissions (from Employee record)
        if (employee != null) {
            // From Rank
            if (employee.getRank() != null) {
                authorities.addAll(employee.getRank().getPermissions().stream()
                        .map(p -> new SimpleGrantedAuthority(p.getNodeString()))
                        .collect(Collectors.toSet()));
            }
            // From Groups
            for (PermissionGroup group : employee.getGroups()) {
                authorities.addAll(group.getPermissions().stream()
                        .map(p -> new SimpleGrantedAuthority(p.getNodeString()))
                        .collect(Collectors.toSet()));
            }
        }
        
        return authorities;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return active; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}