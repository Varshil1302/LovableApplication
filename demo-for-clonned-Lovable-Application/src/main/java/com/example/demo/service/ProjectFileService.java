package com.example.demo.service;

import com.example.demo.dto.project.FileContentResponse;
import com.example.demo.dto.project.FileNode;
import com.example.demo.dto.project.FileNodeResponse;

import java.util.List;

public interface ProjectFileService
{

     FileNodeResponse getFileTree(Long projectId, Long userId);

     FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
