package kz.bitlab.group21boot.repositories;

import kz.bitlab.group21boot.entities.Categories;
import kz.bitlab.group21boot.entities.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    Categories findAllByName(String categories);

}
