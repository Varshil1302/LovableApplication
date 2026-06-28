package com.example.demo.service.impl;

import com.example.demo.dto.project.ProjectRequest;
import com.example.demo.dto.project.ProjectResponse;
import com.example.demo.dto.project.ProjectSummaryResponse;
import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectMember;
import com.example.demo.entity.ProjectMemberId;
import com.example.demo.entity.User;
import com.example.demo.enums.ProjectRole;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.MemberResponseRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.ProjectService;
import com.example.demo.service.ProjectTemplateService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImp implements ProjectService
{

    ProjectRepository projectRepository;
    UserRepository userRepository;
    MemberResponseRepository memberResponseRepository;
    ProjectMapper projectMapper;
    JwtService jwtService;
    ProjectTemplateService projectTemplateService;

    @Override
    public List<ProjectSummaryResponse> getUserProjects()
    {
        Long userId=jwtService.getCurrentUser();
        List<ProjectRepository.ProjectWithRole> projectList=projectRepository.findAllByUserId(userId);
        return projectList.stream().map(pwr->projectMapper.toProjectSummaryResponse(pwr.getProject(),pwr.getUserrole())).toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#id)")
    public ProjectSummaryResponse getProjectDetailsById(Long id)
    {
        Long userId=jwtService.getCurrentUser();
        log.info("dsfassadasca");
        var projectRole=projectRepository.findAccessibleProjectByIdWithRole(id,userId).orElseThrow(()->new BadRequestException(""));
        log.info("safasfwfqw");
        log.info("Role of user:: "+projectRole.getUserrole());
        log.info("dsfsd"+projectRole.getProject().getName());
        //return new ProjectSummaryResponse(Long.valueOf("0"),"aaa",ProjectRole.OWNER, Instant.now(),Instant.now());
        return projectMapper.toProjectSummaryResponse(projectRole.getProject(),projectRole.getUserrole());
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
                                      .project(project)
                                      .user(user)
                                      .role(ProjectRole.OWNER)
                                      .invitedAt(Instant.now())
                                      .acceptedAt(Instant.now())
                                      .build();
        memberResponseRepository.save(projectMember);
        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#id)")
    public ProjectResponse updateProject(Long id, ProjectRequest request)
    {
        Long userId=jwtService.getCurrentUser();
        Project project=projectRepository.findById(id).orElseThrow();
        ProjectMemberId projectMemberId=new ProjectMemberId(project.getId(),userId);
        ProjectMember projectMember=memberResponseRepository.findById(projectMemberId).orElseThrow(()->new ResourceNotFoundException("No Such Records Are Available"));
        if(projectMember.getRole().equals(ProjectRole.VIEWER))
        {
            throw new RuntimeException("Not Allowed To Update");
        }
        project.setName(request.name());
        projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#id)")
    public void softDelete(Long id)
    {
        Long userId=jwtService.getCurrentUser();
        Project project=projectRepository.findById(id).orElseThrow();
        ProjectMemberId projectMemberId=new ProjectMemberId(project.getId(),userId);
        ProjectMember projectMember=memberResponseRepository.findById(projectMemberId).orElseThrow(()->new ResourceNotFoundException("No Such Records Are Available"));
        if(projectMember.getRole().equals(ProjectRole.VIEWER))
        {
            throw new RuntimeException("Not Allowed To Delete");
        }
        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }
}
