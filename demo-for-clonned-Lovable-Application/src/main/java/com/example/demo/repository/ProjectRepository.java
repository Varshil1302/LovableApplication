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
            where p.owner.userId= :userId
            AND p.deletedAt is NULL 
            ORDER BY p.updatedAt DESC
            """)
    List<Project> findAllByUserId(@Param("userId") Long userId);

    @Query("""
            select p from Project p JOIN FETCH p.owner
            where p.owner.userId= :userId
            AND p.id= :projectId
            AND p.deletedAt is NULL
            ORDER BY p.updatedAt DESC
            """)
    Optional<Project> findProjectByUserIdAndProjectId(@Param("userId") Long userId,
                                                      @Param("projectId") Long projectId) ;
}