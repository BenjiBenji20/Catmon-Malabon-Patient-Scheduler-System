package com.azathoth.CatmonMalabonHealthCenter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "admin_name"),
        @UniqueConstraint(columnNames = "email_address")
    }
)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_name", length = 255, nullable = false)
    private String adminName;

    @Column(name = "email_address", nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Admin(Long id, String adminName, String email, String password, Role role) {
        this.id = id;
        this.adminName = adminName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    protected Admin(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", adminName='" + adminName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
