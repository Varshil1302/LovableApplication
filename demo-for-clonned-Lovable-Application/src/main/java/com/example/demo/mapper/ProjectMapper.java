package com.example.demo.mapper;

import com.example.demo.dto.project.ProjectResponse;
import com.example.demo.dto.project.ProjectSummaryResponse;
import com.example.demo.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper
{
     ProjectResponse toProjectResponse(Project project);

     List<ProjectSummaryResponse> toProjectSummury(List<Project> projectList);
}
