package com.sau.project2.service;

import lombok.RequiredArgsConstructor;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class HdfsService {

    private final FileSystem hdfs;

    @Value("${hdfs.image-dir}")
    private String imageDir;

    // WRITE: browser upload -> HDFS
    public void upload(MultipartFile file, String fileName) throws IOException {
        try (InputStream in = file.getInputStream();
             FSDataOutputStream out = hdfs.create(new Path(imageDir, fileName), true)) {
            in.transferTo(out);
        }
    }

    // READ: HDFS -> stream of bytes
    public InputStream open(String fileName) throws IOException {
        return hdfs.open(new Path(imageDir, fileName));
    }

    public boolean exists(String fileName) throws IOException {
        return hdfs.exists(new Path(imageDir, fileName));
    }
}