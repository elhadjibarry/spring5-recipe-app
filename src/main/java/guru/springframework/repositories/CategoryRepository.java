package guru.springframework.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import guru.springframework.domain.Category;
import guru.springframework.domain.Recipe;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findByDescription(String desciption);
}
