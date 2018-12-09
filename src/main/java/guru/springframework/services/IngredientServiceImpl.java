package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand, IngredientCommandToIngredient ingredientCommandToIngredient, RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository, IngredientRepository ingredientRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.ingredientRepository = ingredientRepository;
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


    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand ingredientCommand) {

        Recipe recipe = recipeRepository.findById(ingredientCommand.getRecipeId()).orElse(null);

        if (recipe != null) {

            Ingredient ingredientToUpdate;
            if (ingredientCommand.getId() != null) {
                ingredientToUpdate = recipe.getIngredients()
                        .stream()
                        .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Unkown ingredient id " + ingredientCommand.getId()));

                ingredientToUpdate.setAmount(ingredientCommand.getAmount());
                ingredientToUpdate.setDescription(ingredientCommand.getDescription());
                ingredientToUpdate.setUnitOfMeasure(unitOfMeasureRepository.findById(ingredientCommand.getUnitOfMeasure().getId())
                        .orElseThrow(() -> new RuntimeException("Unknown unit of measure")));
            } else {
                ingredientToUpdate = ingredientCommandToIngredient.convert(ingredientCommand);
                ingredientToUpdate.setRecipe(recipe);
            }

            ingredientToUpdate = ingredientRepository.save(ingredientToUpdate);
            return ingredientToIngredientCommand.convert(ingredientToUpdate);

        } else {
            throw new RuntimeException("Unkown recipe id " + ingredientCommand.getRecipeId());
        }
    }

    @Override
    @Transactional
    public void deleteByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {

        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);

        if (recipe != null) {
            Ingredient ingredientToDelete = recipe.getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Unkown ingredient id " + ingredientId));

            recipe.remove(ingredientToDelete);
            recipeRepository.save(recipe);
        } else {
            throw new RuntimeException("Unkown recipe id " + recipeId);
        }
    }
}
