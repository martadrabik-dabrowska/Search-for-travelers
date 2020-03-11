package com.search.risk.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "role")
public class Role {

    public static final String admin = "admin";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private int id;

    @Column(name = "role")
    @NotNull
    private String role;

    public static String[] getAllRoles() {
        return new String[]{Role.admin};
    }

    public int getId() {
        return this.id;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
