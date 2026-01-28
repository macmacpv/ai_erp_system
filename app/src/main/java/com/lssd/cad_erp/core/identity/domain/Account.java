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

    public boolean hasPermission(String requiredNode) {
        if (root) return true;
        
        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        for (GrantedAuthority auth : authorities) {
            String granted = auth.getAuthority();
            if (granted.equals("*")) return true;
            if (granted.equals(requiredNode)) return true;
            if (granted.endsWith(".*")) {
                String prefix = granted.substring(0, granted.length() - 2);
                if (requiredNode.startsWith(prefix)) return true;
            }
        }
        return false;
    }

    public boolean canManagePermission(String targetNode) {
        if (root) return true;
        
        // Critical: Only root can manage the global wildcard or restricted nodes
        if (targetNode.equals("*") || targetNode.startsWith("sys.") || targetNode.startsWith("restricted.")) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = getAuthorities();
        for (GrantedAuthority auth : authorities) {
            String granted = auth.getAuthority();
            if (granted.endsWith(".permissions.edit")) {
                String scope = granted.replace(".permissions.edit", "");
                if (targetNode.startsWith(scope)) return true;
            }
        }
        
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (root) {
            return Set.of(new SimpleGrantedAuthority("ROLE_ROOT"), 
                          new SimpleGrantedAuthority("ROLE_USER"),
                          new SimpleGrantedAuthority("*"));
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.addAll(directPermissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getNodeString()))
                .collect(Collectors.toSet()));

        if (employee != null) {
            if (employee.getRank() != null) {
                authorities.addAll(employee.getRank().getPermissions().stream()
                        .map(p -> new SimpleGrantedAuthority(p.getNodeString()))
                        .collect(Collectors.toSet()));
            }
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