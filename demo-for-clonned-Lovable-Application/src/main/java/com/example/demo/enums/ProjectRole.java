package com.example.demo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.example.demo.enums.ProjectPermission.*;

@RequiredArgsConstructor
@Getter
public enum ProjectRole
{
    EDITOR(VIEW,EDIT,DELETE),
    VIEWER(VIEW),
    OWNER(VIEW,EDIT,DELETE,MANAGE_MEMBERS,VIEW_MEMBERS);

    ProjectRole(ProjectPermission... projectPermissions)
    {
        this.permissionSet=Set.of(projectPermissions);
    }


    private final Set<ProjectPermission> permissionSet;
}
