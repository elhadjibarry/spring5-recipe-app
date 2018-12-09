package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IngredientControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    IngredientService ingredientService;

    @Mock
    UnitOfMeasureService unitOfMeasureService;

    IngredientController ingredientController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ingredientController = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();
    }

    @Test
    public void listIngredients() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();

        //when
        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);
        mockMvc.perform(get("/recipe/1/ingredients"))
               .andExpect(status().isOk())
               .andExpect(view().name("recipe/ingredient/list"))
               .andExpect(model().attributeExists("recipe"));

        //then
        verify(recipeService).findCommandById(anyLong());
    }

    @Test
    public void showIngredient() throws Exception {
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();

        //when
        when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong())).thenReturn(ingredientCommand);
        mockMvc.perform(get("/recipe/1/ingredient/2/show"))
               .andExpect(status().isOk())
               .andExpect(view().name("recipe/ingredient/show"))
               .andExpect(model().attributeExists("ingredient"));

        //then
        verify(ingredientService).findByRecipeIdAndIngredientId(anyLong(), anyLong());
    }

    @Test
    public void updateIngredientForm() throws Exception {
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();

        //when
        when(ingredientService.findByRecipeIdAndIngredientId(anyLong(), anyLong())).thenReturn(ingredientCommand);
        when(unitOfMeasureService.listAllUnitOfMeasure()).thenReturn(new HashSet<>());

        mockMvc.perform(get("/recipe/1/ingredient/2/update"))
               .andExpect(status().isOk())
               .andExpect(view().name("recipe/ingredient/ingredientform"))
               .andExpect(model().attributeExists("ingredient"))
               .andExpect(model().attributeExists("unitOfMeasureList"));

        //then
        verify(ingredientService).findByRecipeIdAndIngredientId(anyLong(), anyLong());
        verify(unitOfMeasureService).listAllUnitOfMeasure();
    }

    @Test
    public void saveIngredient() throws Exception {
        //given
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setId(3L);
        ingredientCommand.setRecipeId(2L);

        //when
        when(ingredientService.saveIngredientCommand(any())).thenReturn(ingredientCommand);

        mockMvc.perform(post("/recipe/2/ingredient").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                    .param("id", "")
                                                    .param("description", "some " + "string"))
               .andExpect(status().is3xxRedirection())
               .andExpect(view().name("redirect:/recipe/2/ingredient/3/show"));

        //then
        verify(ingredientService).saveIngredientCommand(any(IngredientCommand.class));
    }

    @Test
    public void newIngredientForm() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        //when
        when(recipeService.findCommandById(anyLong())).thenReturn(recipeCommand);
        when(unitOfMeasureService.listAllUnitOfMeasure()).thenReturn(new HashSet<>());

        mockMvc.perform(get("/recipe/1/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("unitOfMeasureList"));

        //then
        verify(recipeService).findCommandById(anyLong());
        verify(unitOfMeasureService).listAllUnitOfMeasure();
    }

    @Test
    public void deleteIngredient() throws Exception {

        mockMvc.perform(get("/recipe/1/ingredient/2/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/1/ingredients"));

        verify(ingredientService).deleteByRecipeIdAndIngredientId(anyLong(), anyLong());
    }
}
