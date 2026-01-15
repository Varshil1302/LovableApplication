package com.example.demo.repository;

import com.example.demo.entity.ProjectMember;
import com.example.demo.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberResponseRepository extends JpaRepository<ProjectMember, ProjectMemberId>
{
     @Query("""
            SELECT pm FROM ProjectMember pm 
            where pm.project.id= :projectId
            ORDER BY pm.invitedAt DESC
             """)
     List<ProjectMember> findAllProjctMemberById(Long projectId);
}
