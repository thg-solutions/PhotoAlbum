package de.thg.photoalbum.controller;

import de.thg.photoalbum.model.Image;
import de.thg.photoalbum.repositories.ImageRepository;
import de.thg.photoalbum.services.PhotoAlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhotoAlbumController.class)
class PhotoAlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhotoAlbumService photoAlbumService;

    @MockBean
    private ImageRepository imageRepository;

    @Test
    void getAllImages() throws Exception {
        when(imageRepository.findAll()).thenReturn(createResultList());
        mockMvc.perform(get("/photoalbum/photos")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        verify(imageRepository).findAll();
    }

    @Test
    void getImageByNameStartingWith() {
    }

    @Test
    void creationDateNotFound() throws Exception {
        when(imageRepository.findById("heute")).thenReturn(Optional.empty());
        mockMvc.perform(get("/photoalbum/photos/search/creationdate").param("creationDate", "heute")).andDo(print()).andExpect(status().isNotFound());
        verify(imageRepository).findById("heute");
    }

    @Test
    void creationDateFound() throws Exception {
        Image image = createImage("heute");
        when(imageRepository.findById("heute")).thenReturn(Optional.of(image));
        mockMvc.perform(get("/photoalbum/photos/search/creationdate").param("creationDate", "heute")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.creationDate").value("heute"));
        verify(imageRepository).findById("heute");
    }

    @Test
    void createOrUpdatePhotoAlbum() {
    }

    @Test
    void analyseImage() throws Exception {
        when(photoAlbumService.analyseImage(any(), anyString())).thenReturn(Optional.of(createImage("heute")));
        Resource resource = new ClassPathResource("testdata/PHOTO0021.JPG");
        MockMultipartFile multipartFile = new MockMultipartFile("file", "PHOTO0021.JPG", "image/jpeg", resource.getInputStream());
        mockMvc.perform(multipart("/photoalbum/analyse").file(multipartFile)).andDo(print()).andExpect(status().isOk());
        verify(photoAlbumService).analyseImage(any(), anyString());
    }

    private Image createImage(String creationDate) {
        Image image = new Image();
        image.setCreationDate(creationDate);
        return image;
    }

    private List<Image> createResultList() {
        Image image1 = createImage("2020-04-28");
        Image image2 = createImage("2020-04-27");
        return Arrays.asList(image1, image2);
    }
}