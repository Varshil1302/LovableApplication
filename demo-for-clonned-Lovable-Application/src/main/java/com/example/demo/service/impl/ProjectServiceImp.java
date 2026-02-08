package com.example.demo.service.impl;

import com.example.demo.dto.project.ProjectRequest;
import com.example.demo.dto.project.ProjectResponse;
import com.example.demo.dto.project.ProjectSummaryResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectMember;
import com.example.demo.entity.ProjectMemberId;
import com.example.demo.entity.User;
import com.example.demo.enums.ProjectRole;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.MemberResponseRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
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
    MemberResponseRepository memberResponseRepository;
    ProjectMapper projectMapper;
    JwtService jwtService;

    @Override
    public List<ProjectSummaryResponse> getUserProjects()
    {
        Long userId=jwtService.getCurrentUser();
        List<Project> projectList=projectRepository.findAllByUserId(userId);
        return projectMapper.toProjectSummury(projectList);
    }

    @Override
    public ProjectResponse getProjectDetailsById(Long id)
    {
        Long userId=jwtService.getCurrentUser();
        Project project=projectRepository.findProjectByUserIdAndProjectId(userId,id).orElseThrow(()->new ResourceNotFoundException("User with Id: "+userId+" is not member of this project "+id));
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request)
    {
        Long userId=jwtService.getCurrentUser();
        User user=userRepository.findById(userId).orElseThrow();
        Project project=Project.builder()
                .name(request.name())
                .isPublic(false)
                             .build();


        projectRepository.save(project);

        ProjectMemberId projectMemberId=new ProjectMemberId(project.getId(),userId);
        ProjectMember projectMember=ProjectMember.builder()
                                      .id(projectMemberId)
                                      .project(project).user(user).projectRole(ProjectRole.OWNER)
                                      .invitedAt(Instant.now())
                                      .acceptedAt(Instant.now())
                                      .build();
        memberResponseRepository.save(projectMember);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request)
    {
        Long userId=jwtService.getCurrentUser();
        Project project=projectRepository.findById(id).orElseThrow();
        ProjectMemberId projectMemberId=new ProjectMemberId(project.getId(),userId);
        ProjectMember projectMember=memberResponseRepository.findById(projectMemberId).orElseThrow(()->new ResourceNotFoundException("No Such Records Are Available"));
        if(projectMember.getProjectRole().equals(ProjectRole.VIEWER))
        {
            throw new RuntimeException("Not Allowed To Update");
        }
        project.setName(request.name());
        projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id)
    {
        Long userId=jwtService.getCurrentUser();
        Project project=projectRepository.findById(id).orElseThrow();
        ProjectMemberId projectMemberId=new ProjectMemberId(project.getId(),userId);
        ProjectMember projectMember=memberResponseRepository.findById(projectMemberId).orElseThrow(()->new ResourceNotFoundException("No Such Records Are Available"));
        if(projectMember.getProjectRole().equals(ProjectRole.VIEWER))
        {
            throw new RuntimeException("Not Allowed To Delete");
        }
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }
}
