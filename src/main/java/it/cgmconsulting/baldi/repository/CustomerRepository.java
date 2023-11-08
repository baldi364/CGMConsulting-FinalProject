package it.cgmconsulting.baldi.repository;

import it.cgmconsulting.baldi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>
{

}
