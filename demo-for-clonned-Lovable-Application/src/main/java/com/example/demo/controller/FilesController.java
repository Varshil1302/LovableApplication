package com.example.demo.controller;

import com.example.demo.dto.project.FileContentResponse;
import com.example.demo.dto.project.FileNode;
import com.example.demo.security.JwtService;
import com.example.demo.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FilesController
{
    private final ProjectFileService projectFileService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<FileNode>> getFileTree(@PathVariable Long projectId) {
        Long userId = jwtService.getCurrentUser();
        return ResponseEntity.ok(projectFileService.getFileTree(projectId, userId));
    }

    @GetMapping("{projectId}/{*path}") // /src/hooks/get-user-hook.jsx
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @PathVariable String path
    ) {
        Long userId = jwtService.getCurrentUser();
        return ResponseEntity.ok(projectFileService.getFileContent(projectId, path));
    }



}
