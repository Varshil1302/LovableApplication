package com.example.demo.controller;

import com.example.demo.dto.member.InviteMemberRequest;
import com.example.demo.dto.member.MemberResponse;
import com.example.demo.dto.member.UpdateMemberRoleRequest;
import com.example.demo.entity.ProjectMember;
import com.example.demo.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{id}/members")
@RequiredArgsConstructor
public class ProjectMemberController
{

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable("id") Long projectId) {
        Long userId = 1L;
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId, userId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable("id") Long projectId,
            @RequestBody InviteMemberRequest request
    ) {
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(
                projectMemberService.inviteMember(projectId, request, userId)
        );
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable("id") Long projectId,
            @PathVariable Long memberId,
            @RequestBody UpdateMemberRoleRequest request
    ) {
        Long userId = 1L;
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, request, userId));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMemberRole(
            @PathVariable("id") Long projectId,
            @PathVariable Long memberId
    ) {
        Long userId = 1L;
        projectMemberService.deleteProjectMember(projectId, memberId, userId);
        return ResponseEntity.noContent().build();
    }

}
