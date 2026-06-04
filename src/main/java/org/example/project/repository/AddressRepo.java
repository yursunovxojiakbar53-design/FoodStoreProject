package org.example.project.repository;

import org.example.project.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepo extends JpaRepository<Address, Integer> {
    Optional<Address> findByIdAndUserId(Integer addressId, Integer id);

    List<Address> findAllByUserId(Integer id);
}
