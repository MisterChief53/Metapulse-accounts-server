package com.metapulse.accountsserver;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    /*Saves the received image, because we only need one image per time, we delete the last image
    * before inserting the next one, then, we save the byte stream of the file*/
    @Transactional()
    public void saveImage(MultipartFile file) throws IOException {

        Optional<Image> lastImage = imageRepository.findFirstByOrderByIdAsc();
        lastImage.ifPresent(image -> {
            // Delete the last image, only allowing to save one image at a time
            imageRepository.delete(image);
        });
        System.out.println(file.getContentType());
        Image image = new Image();
        image.setContenido(file.getBytes());
        imageRepository.save(image);
    }

    /*It returns the last image to be saved in table*/
    @Transactional(readOnly = true)
    public Image getImage(){
        Optional<Image> image = imageRepository.findFirstByOrderByIdAsc();
        return image.orElse(null);
    }



}
