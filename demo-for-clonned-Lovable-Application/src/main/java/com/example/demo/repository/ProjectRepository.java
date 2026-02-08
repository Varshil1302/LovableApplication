package com.example.demo.repository;


import com.example.demo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long>
{
    @Query("""
            select p from Project p 
            where p.deletedAt is NULL 
            AND EXISTS(
              SELECT 1 FROM ProjectMember pm
              WHERE pm.id.userId= :userId
              AND pm.id.projectId=p.id
            )
            ORDER BY p.updatedAt DESC
            """)
    List<Project> findAllByUserId(@Param("userId") Long userId);

    @Query("""
            select p from Project p 
            where p.deletedAt is NULL 
            AND EXISTS(
              SELECT 1 FROM ProjectMember pm
              WHERE pm.id.userId= :userId
              AND pm.id.projectId=:projectId
            )
            ORDER BY p.updatedAt DESC
            """)
    Optional<Project> findProjectByUserIdAndProjectId(@Param("userId") Long userId,
                                                      @Param("projectId") Long projectId) ;
}