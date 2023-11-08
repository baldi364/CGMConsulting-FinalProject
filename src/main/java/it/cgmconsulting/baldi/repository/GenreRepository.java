package it.cgmconsulting.baldi.repository;

import it.cgmconsulting.baldi.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository <Genre, Long>
{

}
