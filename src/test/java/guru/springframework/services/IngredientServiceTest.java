package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.IngredientRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class IngredientServiceTest {

    IngredientToIngredientCommand ingredientToIngredientCommand;
    IngredientCommandToIngredient ingredientCommandToIngredient;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    UnitOfMeasureRepository unitOfMeasureRepository;

    @Mock
    IngredientRepository ingredientRepository;

    IngredientService ingredientService;

    @Before
    public void setUp() throws Exception {
        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
        ingredientCommandToIngredient = new IngredientCommandToIngredient(new UnitOfMeasureCommandToUnitOfMeasure());
        ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand, ingredientCommandToIngredient, recipeRepository, unitOfMeasureRepository, ingredientRepository);
    }

    @Test
    public void findByRecipeIdAndIngredientId() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId(3L);

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        //when
        when(recipeRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(recipe));
        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId(1L, 3L);

        //then
        assertThat(ingredientCommand.getId()).isEqualTo(3L);
        assertThat(ingredientCommand.getRecipeId()).isEqualTo(1L);
        verify(recipeRepository).findById(anyLong());
    }

    @Test
    public void saveRecipeCommand() {
        //given
        UnitOfMeasureCommand unitOfMeasureCommand = new UnitOfMeasureCommand();
        unitOfMeasureCommand.setId(1L);

        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(3L);
        ingredientCommand.setRecipeId(2L);
        ingredientCommand.setUnitOfMeasure(unitOfMeasureCommand);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(3L);

        Recipe recipe = new Recipe();
        recipe.setId(2L);
        recipe.addIngredient(ingredient);

        Optional<Recipe> recipeOptional = Optional.of(recipe);

        //when
        when(recipeRepository.findById(anyLong())).thenReturn(recipeOptional);
        when(unitOfMeasureRepository.findById(anyLong())).thenReturn(Optional.of(new UnitOfMeasure()));
        when(ingredientRepository.save(any())).thenReturn(ingredient);
        IngredientCommand savedIngredientCommand = ingredientService.saveIngredientCommand(ingredientCommand);

        assertThat(savedIngredientCommand.getId()).isEqualTo(3L);
        verify(recipeRepository).findById(anyLong());
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    public void deleteByRecipeIdAndIngredientId() {

        //given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId(3L);

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        //when
        when(recipeRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(recipe));
        ingredientService.deleteByRecipeIdAndIngredientId(1L, 3L);

        //then
        verify(recipeRepository).findById(anyLong());
        verify(recipeRepository).save(any());
    }
}
