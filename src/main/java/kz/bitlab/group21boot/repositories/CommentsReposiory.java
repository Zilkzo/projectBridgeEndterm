package kz.bitlab.group21boot.repositories;

import kz.bitlab.group21boot.entities.Comments;
import kz.bitlab.group21boot.entities.Items;
import kz.bitlab.group21boot.entities.NewPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentsReposiory extends JpaRepository<Comments, Long> {
    List<Comments> findAllByNewPost(NewPost newPost);
    Optional<Comments> findById(Long id);
}
