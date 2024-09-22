package com.clv.kanbanapp.service;


import com.clv.kanbanapp.dto.response.ResponseData;
import com.clv.kanbanapp.dto.response.ServiceResponse;
import com.clv.kanbanapp.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public ServiceResponse<?> deleteImage(Long id) {
        imageRepository.deleteById(id);
        return ServiceResponse.builder()
                        .statusCode(HttpStatus.OK)
                        .status(ResponseStatus.SUCCESS.toString())
                        .message("Image deleted successfully")
                        .build();
    }
}
