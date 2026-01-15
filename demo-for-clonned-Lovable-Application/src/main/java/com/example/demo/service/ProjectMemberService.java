package com.example.demo.service;

import com.example.demo.dto.member.InviteMemberRequest;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.member.UpdateMemberRoleRequest;
import com.example.demo.entity.ProjectMember;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService
{

     List<MemberResponse> getProjectMembers(Long projectId, Long userId);

     MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId);

     MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId);

     void deleteProjectMember(Long projectId, Long memberId, Long userId);
}
