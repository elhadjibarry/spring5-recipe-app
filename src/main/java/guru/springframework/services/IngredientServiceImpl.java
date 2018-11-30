package guru.springframework.services;

import org.springframework.stereotype.Service;
import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;

/**
 *
 */
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final RecipeRepository recipeRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand, RecipeRepository recipeRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {

        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);

        if (recipe != null) {
            IngredientCommand ingredientCommand = recipe.getIngredients()
                                                        .stream()
                                                        .filter(ingredient -> ingredient.getId().equals(ingredientId))
                                                        .map(ingredientToIngredientCommand::convert)
                                                        .findFirst()
                                                        .orElse(null);
            return ingredientCommand;
        } else {
            throw new RuntimeException("Unkown recipe id " + recipeId);
        }
    }
}
