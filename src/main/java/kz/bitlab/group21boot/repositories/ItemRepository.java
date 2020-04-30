package kz.bitlab.group21boot.repositories;

import kz.bitlab.group21boot.entities.Categories;
import kz.bitlab.group21boot.entities.Items;
import kz.bitlab.group21boot.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Items, Long> {
    List<Items> findAllByAuthor(Users author);
    List<Items> findAllByCategories(Categories cat);
}