package it.cgmconsulting.baldi.repository;

import it.cgmconsulting.baldi.entity.FilmStaff;
import it.cgmconsulting.baldi.entity.FilmStaffId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmStaffRepository extends JpaRepository<FilmStaff, FilmStaffId>
{

}
