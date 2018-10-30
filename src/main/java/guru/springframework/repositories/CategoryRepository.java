package guru.springframework.repositories;

import org.springframework.data.repository.CrudRepository;
import guru.springframework.domain.Category;
import guru.springframework.domain.Recipe;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
