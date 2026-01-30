package com.gwangjin.issuerwas.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    @Column(name = "customer_ref")
    private UUID customerRef;

    private String username;
    private String name;
    private String birth;
    private String phone;
    private String password;

    public Customer(UUID customerRef, String username, String name, String birth, String phone, String password) {
        this.customerRef = customerRef;
        this.username = username;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.password = password;
    }
}
