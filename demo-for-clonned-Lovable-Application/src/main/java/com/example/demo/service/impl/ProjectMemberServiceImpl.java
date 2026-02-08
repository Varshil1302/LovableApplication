package com.example.demo.service.impl;

import com.example.demo.dto.member.InviteMemberRequest;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.member.UpdateMemberRoleRequest;
import com.example.demo.entity.Project;
import com.example.demo.entity.ProjectMember;
import com.example.demo.entity.ProjectMemberId;
import com.example.demo.entity.User;
import com.example.demo.enums.ProjectRole;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.MemberResponseMapper;
import com.example.demo.repository.MemberResponseRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.ProjectMemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ProjectMemberServiceImpl implements ProjectMemberService
{
    ProjectRepository projectRepository;
    UserRepository userRepository;
    MemberResponseRepository memberResponseRepository;
    MemberResponseMapper memberResponseMapper;
    JwtService jwtService;

    @Override
    public List<MemberResponse> getProjectMembers(Long projectId)
    {
        Long userId=jwtService.getCurrentUser();
        Project project=projectRepository.findProjectByUserIdAndProjectId(userId,projectId).orElseThrow();
        List<MemberResponse> lstMemberResponse=new ArrayList<>();
        List<ProjectMember> projectMembers=memberResponseRepository.findAllProjctMemberById(projectId);
        List<MemberResponse> lsttemp=memberResponseMapper.toMemberResponse(projectMembers);
        lstMemberResponse.addAll(lsttemp);
        return lstMemberResponse;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request)
    {
        Long userId=jwtService.getCurrentUser();
        Project project= projectRepository.findProjectByUserIdAndProjectId(userId,projectId).orElseThrow(()->new RuntimeException("dsvdsfadscas"));
        User invitee= userRepository.findByEmail(request.email()).orElseThrow(()->new RuntimeException("dsvdsfadscas"));
        ProjectMemberId projectMemberId1=new ProjectMemberId(project.getId(),userId);
        ProjectMember projectMember1=memberResponseRepository.findById(projectMemberId1).orElseThrow(()->new ResourceNotFoundException("No Such Records Are Available"));
        if(!projectMember1.getProjectRole().equals(ProjectRole.OWNER))
        {
            throw new RuntimeException("Owner should not be invitee");
        }
        ProjectMemberId projectMemberId=new ProjectMemberId(projectId,invitee.getUserId());
        if(memberResponseRepository.existsById(projectMemberId)){
            throw new RuntimeException("Memeber is already in project....");
        }
        ProjectMember projectMember=ProjectMember.builder()
                                     .id(projectMemberId)
                                     .user(invitee)
                                     .project(project)
                                     .projectRole(request.role())
                                     .invitedAt(Instant.now())
                                     .build();
        memberResponseRepository.save(projectMember);
        return memberResponseMapper.toMemberResponse(projectMember);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request)
    {

        List<ProjectMember> projectMemberList=memberResponseRepository.findAllProjctMemberById(projectId);
        if(!projectMemberList.stream().map(pm->pm.getId().getUserId()).toList().contains(memberId))
        {
            throw new RuntimeException("Member is not part of this project...");
        }
        ProjectMember updatableMember=projectMemberList.stream().filter(pm->pm.getId().getUserId()==memberId).findAny().get();
        updatableMember.setProjectRole(ProjectRole.VIEWER);
        memberResponseRepository.save(updatableMember);
        return memberResponseMapper.toMemberResponse(updatableMember);
    }

    @Override
    public void deleteProjectMember(Long projectId, Long memberId)
    {
        List<ProjectMember> projectMemberList=memberResponseRepository.findAllProjctMemberById(projectId);
        if(!projectMemberList.stream().map(pm->pm.getId().getUserId()).toList().contains(memberId))
        {
            throw new RuntimeException("Member is not part of this project...");
        }
        ProjectMember deletedMember=projectMemberList.stream().filter(pm->pm.getId().getUserId()==memberId).findAny().get();
        memberResponseRepository.delete(deletedMember);
    }
}
