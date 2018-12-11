package guru.springframework.services;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import guru.springframework.commands.IngredientCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final RecipeRepository recipeRepository;

    public ImageServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void saveImageFile(Long recipeId, MultipartFile file) {
        log.debug("Save image command for recipe " + recipeId);

        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);

        if (recipe != null) {
            try {
                Byte[] imageBytes = new Byte[file.getBytes().length];
                int i = 0;

                for(byte b : file.getBytes()) {
                    imageBytes[i++] = b;
                }

                recipe.setImage(imageBytes);
                recipeRepository.save(recipe);

            } catch (IOException e) {
                log.error("Error when reading file", e);
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Unkown recipe id " + recipeId);
        }
    }
}
