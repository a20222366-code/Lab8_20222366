package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sw2.lab5.Entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {


}
