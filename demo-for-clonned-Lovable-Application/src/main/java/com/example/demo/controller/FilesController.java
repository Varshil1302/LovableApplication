package com.example.demo.controller;

import com.example.demo.dto.project.FileContentResponse;
import com.example.demo.dto.project.FileNode;
import com.example.demo.dto.project.FileNodeResponse;
import com.example.demo.security.JwtService;
import com.example.demo.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/files")
@RequiredArgsConstructor
public class FilesController
{
    private final ProjectFileService projectFileService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<FileNodeResponse> getFileTree(@PathVariable Long projectId) {
        Long userId = jwtService.getCurrentUser();
        return ResponseEntity.ok(projectFileService.getFileTree(projectId, userId));
    }

    @GetMapping("/content") // /src/hooks/get-user-hook.jsx
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @RequestParam String path
    ) {
        Long userId = jwtService.getCurrentUser();
        return ResponseEntity.ok(projectFileService.getFileContent(projectId, path));
    }



}
