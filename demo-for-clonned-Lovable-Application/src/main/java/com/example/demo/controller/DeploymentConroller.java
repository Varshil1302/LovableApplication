package com.example.demo.controller;

import com.example.demo.dto.deploye.DeployementResponse;
import com.example.demo.service.DeployementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deploy/project/")
@RequiredArgsConstructor
public class DeploymentConroller {

    private final DeployementService deployementService;

    @PostMapping("/{projectId}")
    public ResponseEntity<DeployementResponse> deployeProject(@PathVariable Long projectId)
    {
        return ResponseEntity.ok(deployementService.deployeProject(projectId));
    }

}
