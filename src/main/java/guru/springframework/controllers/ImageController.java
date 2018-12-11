package guru.springframework.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.ImageService;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ImageController {

    private final RecipeService recipeService;
    private final ImageService imageService;

    public ImageController(RecipeService recipeService, ImageService imageService) {
        this.recipeService = recipeService;
        this.imageService = imageService;
    }

    @GetMapping("/recipe/{recipeId}/image")
    public String getUploadImageForm(Model model, @PathVariable String recipeId) {
        log.debug("Loading upload image form for recipe " + recipeId);
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(recipeId)));
        return "recipe/imageuploadform";
    }


    @PostMapping("/recipe/{recipeId}/image")
    public String saveImage(@PathVariable String recipeId, @RequestParam("imagefile") MultipartFile multipartFile) {
        log.debug("saving upload image form for recipe " + recipeId);
        imageService.saveImageFile(Long.valueOf(recipeId), multipartFile );
        return "redirect:/recipe/" + recipeId + "/show";
    }

    @GetMapping("/recipe/{recipeId}/recipeimage")
    public void getImageFromDatabase(Model model, @PathVariable String recipeId, HttpServletResponse response) throws IOException {
        log.debug("rendering image for recipe " + recipeId);
        RecipeCommand recipeCommand = recipeService.findCommandById(Long.valueOf(recipeId));

        byte[] imageBytes = new byte[recipeCommand.getImage().length];
        int i = 0;

        for(Byte b : recipeCommand.getImage()) {
            imageBytes[i++] = b;
        }

        response.setContentType("image/jpeg");
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        IOUtils.copy(inputStream, response.getOutputStream());
    }
}
