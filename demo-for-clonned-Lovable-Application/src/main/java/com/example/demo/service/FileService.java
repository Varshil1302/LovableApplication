package com.example.demo.service;

import com.example.demo.dto.project.FileContentResponse;
import com.example.demo.dto.project.FileNode;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface FileService
{

     List<FileNode> getFileTree(Long projectId, Long userId);

     FileContentResponse getFileContent(Long projectId, String path, Long userId);
}
