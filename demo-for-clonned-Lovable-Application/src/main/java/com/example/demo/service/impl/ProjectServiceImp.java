package com.example.demo.service.impl;

import com.example.demo.dto.project.ProjectRequest;
import com.example.demo.dto.project.ProjectResponse;
import com.example.demo.dto.project.ProjectSummaryResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProjectServiceImp implements ProjectService
{

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId)
    {
        List<Project> projectList=projectRepository.findAllByUserId(userId);
        return projectMapper.toProjectSummury(projectList);
    }

    @Override
    public ProjectResponse getProjectDetailsById(Long id, Long userId)
    {
        Project project=projectRepository.findProjectByUserIdAndProjectId(userId,id).orElseThrow();
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {

        User user=userRepository.findById(userId).orElseThrow();
        Project project=Project.builder()
                .name(request.name()).owner(user)
                .isPublic(false)
                             .build();
        projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId)
    {
        Project project=projectRepository.findById(id).orElseThrow();
        if(!project.getOwner().getUserId().equals(userId))
        {
            throw new RuntimeException("Not Allowed To Update");
        }
        project.setName(request.name());
        projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId)
    {
        Project project=projectRepository.findById(id).orElseThrow();
        if(!project.getOwner().getUserId().equals(userId))
        {
            throw new RuntimeException("Not Allowed To Delete");
        }
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }
}
