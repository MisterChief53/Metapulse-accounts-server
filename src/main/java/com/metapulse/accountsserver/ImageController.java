package com.metapulse.accountsserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            imageService.saveImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfully saved.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There has been an error while saving the image");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getImgFile() {
        Image imagen = imageService.getImage();

        if (imagen != null) {
            return ResponseEntity.ok().body(imagen.getContenido());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There is not any image in database");
        }
    }
}
