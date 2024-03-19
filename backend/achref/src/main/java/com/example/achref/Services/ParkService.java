package com.example.achref.Services;


import com.example.achref.Entities.parking.Etage;
import com.example.achref.Entities.parking.Parking;
import com.example.achref.Repositories.parking.BlocRepository;
import com.example.achref.Repositories.parking.EtageRepository;
import com.example.achref.Repositories.parking.ParkingRepository;
import com.example.achref.Repositories.parking.PlaceParkingRepository;
import com.example.achref.Repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkService {
    private final ParkingRepository parkingRepository;
    private final BlocRepository blocRepository;
    private final EtageRepository etageRepository;
    private final PlaceParkingRepository placeParkingRepository;

    public void addParking(Parking parking) {
        parkingRepository.save(parking);
    }

    public List<Parking> displayParking() {
        return parkingRepository.findAll();
    }

    public void updateParking(Parking parking) {
        Parking p = parkingRepository.findById(parking.getId()).orElse(null);
        if (p.getEtages() != null) {
            parking.setEtages(p.getEtages());
        }

        parkingRepository.save(parking);
    }

    public void deleteParking(Long id) {
        Optional<Parking> parkingOptional = parkingRepository.findById(id);
        if (parkingOptional.isPresent()) {
            parkingRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Le parking avec l'ID " + id + " n'existe pas !");
        }
    }

    public void addEtageToParking(Long parkingId, Etage etage) {
        Parking parking = parkingRepository.findById(parkingId)
                .orElseThrow(() -> new IllegalArgumentException("Parking non trouvé avec l'ID : " + parkingId));

        // Vérification de la capacité totale des étages
        int totalCapacity = parking.getEtages().stream()
                .mapToInt(Etage::getCapaciteEtage)
                .sum();
        if (totalCapacity + etage.getCapaciteEtage() > parking.getCapaciteTotale()) {
            throw new IllegalArgumentException("La capacité totale des étages dépasse la capacité totale du parking.");
        }

        // Ajout de l'étage au parking
        etage.setParking(parking);
        parking.getEtages().add(etage);
        parkingRepository.save(parking);
    }


    public void updateEtage(Long etageId, Etage updatedEtage) {
        Etage etage = etageRepository.findById(etageId)
                .orElseThrow(() -> new IllegalArgumentException("Étage non trouvé avec l'ID : " + etageId));

        // Calcul de la capacité totale des étages après la mise à jour
        Parking parking = etage.getParking();
        int totalCapacity = parking.getEtages().stream()
                .mapToInt(Etage::getCapaciteEtage)
                .sum();
        int updatedTotalCapacity = totalCapacity - etage.getCapaciteEtage() + updatedEtage.getCapaciteEtage();

        // Vérification si la capacité totale des étages dépasse la capacité totale du parking
        if (updatedTotalCapacity > parking.getCapaciteTotale()) {
            throw new IllegalArgumentException("La capacité totale des étages dépasse la capacité totale du parking.");
        }

        // Mise à jour des détails de l'étage
        etage.setNumeroEtage(updatedEtage.getNumeroEtage());
        etage.setCapaciteEtage(updatedEtage.getCapaciteEtage());

        etageRepository.save(etage);
    }
    public List<Etage> displayEtage() {return etageRepository.findAll();}






}
