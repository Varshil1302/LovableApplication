package com.example.demo.security;

import com.example.demo.enums.ProjectPermission;
import com.example.demo.enums.ProjectRole;
import com.example.demo.repository.MemberResponseRepository;
import com.example.demo.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpression
{

    private final MemberResponseRepository memberResponseRepository;
    private final JwtService jwtService;

    private boolean accessMethod(Long projectId, ProjectPermission projectPermission)
    {
        Long userId= jwtService.getCurrentUser();

        return memberResponseRepository.findRoleByUserIdAndProjectId(userId,projectId)
                .map(role->role.getPermissionSet().contains(projectPermission))
                .orElse(false);
    }

    public boolean canEditProject(Long projectId)
    {
        return accessMethod(projectId,ProjectPermission.EDIT);
    }
    public boolean canViewProject(Long projectId)
    {
        return accessMethod(projectId,ProjectPermission.VIEW);
    }
    public boolean canDeleteProject(Long projectId)
    {
        return accessMethod(projectId,ProjectPermission.DELETE);
    }

    public boolean canManageMembersProject(Long projectId)
    {
        return accessMethod(projectId,ProjectPermission.MANAGE_MEMBERS);
    }
    public boolean canViewMembersProject(Long projectId)
    {
        return accessMethod(projectId,ProjectPermission.VIEW_MEMBERS);
    }

}
