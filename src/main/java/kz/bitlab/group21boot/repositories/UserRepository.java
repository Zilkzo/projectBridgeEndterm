package kz.bitlab.group21boot.repositories;

import kz.bitlab.group21boot.entities.Roles;
import kz.bitlab.group21boot.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByEmailAndIsActive(String email, boolean active);
    List<Users> findAllByRoles(Roles u);
    Users findByEmail(String email);

}
