package kz.bitlab.group21boot.repositories;

import kz.bitlab.group21boot.entities.Items;
import kz.bitlab.group21boot.entities.NewPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<NewPost, Long> {
    List<NewPost> findAll();
    Optional<NewPost> findById(Long id);
}
