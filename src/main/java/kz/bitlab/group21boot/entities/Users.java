package kz.bitlab.group21boot.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "isActive")
    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Roles> roles;

    public Users(String email, String password, String fullName, Set<Roles> roles) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.roles = roles;
    }

    public Users(String email, String password, String fullName, boolean isActive, Set<Roles> roles) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.isActive = isActive;
        this.roles = roles;
    }
}
