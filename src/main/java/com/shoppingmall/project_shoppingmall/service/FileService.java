package com.shoppingmall.project_shoppingmall.service;

import lombok.extern.java.Log;
import net.coobird.thumbnailator.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);
        if(deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
    public String createThumbnail(String uploadPath, String originalFilename, byte[] fileData) throws IOException {
        UUID uuid = UUID.randomUUID();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
        ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(100, 100)
                .toOutputStream(thumbnailOutputStream);

        String thumbnailFilename = "thumb_" +uuid.toString() + originalFilename;
        Path thumbnailPath = Paths.get(uploadPath, thumbnailFilename);
        Files.write(thumbnailPath, thumbnailOutputStream.toByteArray());
        return thumbnailFilename;
    }


}
