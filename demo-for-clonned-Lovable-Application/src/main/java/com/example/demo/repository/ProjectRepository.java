package com.example.demo.repository;


import com.example.demo.entity.Project;
import com.example.demo.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long>
{
    @Query("""
            select p as project,pm.role as userrole
            from Project p JOIN ProjectMember pm 
            ON p.id=pm.project.id
            where pm.user.userId = :userId
            AND p.deletedAt is NULL 
            ORDER BY p.updatedAt DESC
            """)
    List<ProjectWithRole> findAllByUserId(@Param("userId") Long userId);

    @Query("""
            select p from Project p 
            where p.id = :projectId
             AND p.deletedAt is NULL 
            AND EXISTS(
              SELECT 1 FROM ProjectMember pm
              WHERE pm.id.userId= :userId
              AND pm.id.projectId=:projectId
            )
            ORDER BY p.updatedAt DESC
            """)
    Optional<Project> findProjectByUserIdAndProjectId(@Param("userId") Long userId,
                                                      @Param("projectId") Long projectId) ;
   @Query("""
           select p as project, pm.role as userrole from Project p
           JOIN ProjectMember pm 
           ON p.id=pm.project.id 
           where p.id= :projectId
           AND pm.user.userId= :userId
           AND p.deletedAt IS NULL
           """)
   Optional<ProjectWithRole> findAccessibleProjectByIdWithRole(
           @Param("projectId") Long projectId,
           @Param("userId") Long userId
   );

   interface  ProjectWithRole
   {
       Project getProject();
       ProjectRole getUserrole();
   }
}