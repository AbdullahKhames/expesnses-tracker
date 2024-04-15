package name.expenses.features.user.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import name.expenses.features.base.models.BaseModel;
import name.expenses.features.customer.models.Customer;
import org.hibernate.Hibernate;

import java.security.Principal;
import java.util.*;

@Setter
@Getter
@Entity
@ToString(callSuper = true)
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel implements UserDetails, Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String email;
    private int age;
    private String password;
    private boolean verified = true;
    private boolean loggedIn = false;
    private String deviceId;
    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private Customer customer;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private List<Token> tokens;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    private Set<Role> roles = new HashSet<>();
    public User(String email, String encode) {
        this.email = email;
        this.password = encode;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String getName() {
        return email;
    }


}