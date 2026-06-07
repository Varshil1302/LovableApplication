package com.example.demo.service.impl;

import com.example.demo.dto.project.FileContentResponse;
import com.example.demo.dto.project.FileNode;
import com.example.demo.service.ProjectFileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectFileServiceImpl implements ProjectFileService
{

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {

    }
}
