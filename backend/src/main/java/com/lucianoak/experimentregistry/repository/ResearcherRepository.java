package com.lucianoak.experimentregistry.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lucianoak.experimentregistry.model.Researcher;

public interface ResearcherRepository extends JpaRepository<Researcher, UUID> {

  @Query("""
          SELECT r
          FROM Researcher r
          WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))
          ORDER BY
              CASE
                  WHEN LOWER(r.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0
                  ELSE 1
              END,
              r.name
      """)
  List<Researcher> searchByName(@Param("name") String name);

  boolean existsByEmail(String email);

}
