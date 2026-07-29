package ru.otus.java.spring.project.promotion.domains.promotions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users")
@NamedEntityGraph(name = "user-authorities-entity-graph",
        attributeNodes = @NamedAttributeNode("userAuthorities"))
public class User {

    @Id
    private String username;

    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<UserAuthorities> userAuthorities = new ArrayList<>();

    public void addAuthority(String authority) {
        UserAuthorities userAuthorities = new UserAuthorities(this, authority);
        this.userAuthorities.add(userAuthorities);
    }
}
