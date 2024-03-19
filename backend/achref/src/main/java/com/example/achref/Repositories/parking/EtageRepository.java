package com.example.achref.Repositories.parking;

import com.example.achref.Entities.parking.Etage;
import com.example.achref.Entities.parking.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtageRepository extends JpaRepository<Etage, Long> {

}
