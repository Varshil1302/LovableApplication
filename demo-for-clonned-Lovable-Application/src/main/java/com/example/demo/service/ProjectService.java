package com.example.demo.service;

import com.example.demo.dto.project.ProjectRequest;
import com.example.demo.dto.project.ProjectResponse;
import com.example.demo.dto.project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService
{

    List<ProjectSummaryResponse> getUserProjects();

     ProjectResponse getProjectDetailsById(Long id);

     ProjectResponse createProject(ProjectRequest request);

     ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);
}
