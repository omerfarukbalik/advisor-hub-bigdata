package com.sau.project2.controller;

import com.sau.project2.service.HdfsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final HdfsService hdfsService;

    @GetMapping("/images/{name:.+}")
    public ResponseEntity<InputStreamResource> image(@PathVariable String name) throws IOException {
        if (!hdfsService.exists(name)) {
            return ResponseEntity.notFound().build();
        }
        MediaType type = MediaTypeFactory.getMediaType(name).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(type)
                .header("X-Served-From", "HDFS")      // proof for the demo (F12 -> Network)
                .body(new InputStreamResource(hdfsService.open(name)));
    }
}