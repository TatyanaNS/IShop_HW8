package com.ishop.model;

import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Length(min = 5, message = "Email should be at least 5 character.")
    @Email(message = "Email Address")
    @Column(name = "email")
    private String email;

    @Length(min = 5, message = "Password should be at least 5 character.")
    @Column(name = "password")
    private String password;

    @Column(name = "last_name")
    private String lastName;

    @Size(min = 5, message = "First name should be at least 5 character.")
    @Column(name = "first_name")
    private String firstName;

    @NotNull(message = "User has minimum one role!")
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH},
        fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = { @JoinColumn(name = "user_id") },
        inverseJoinColumns = { @JoinColumn(name = "role_id") }
    )
    private List<Role> roles;
}