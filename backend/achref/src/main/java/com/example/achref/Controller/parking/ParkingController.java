package com.example.achref.Controller.parking;

import com.example.achref.Entities.parking.Etage;
import com.example.achref.Entities.parking.Parking;
import com.example.achref.Services.ParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Parking")
public class ParkingController {

    private final ParkService parkService;

    @PostMapping("/addParking")
    public ResponseEntity<String> addParking(@RequestBody Parking parking) {
        parkService.addParking(parking);
        return new ResponseEntity<>("Parking ajouté avec succès !", HttpStatus.OK);
    }
    @GetMapping("/displayParking")
    public List<Parking> displayParking(){
        return parkService.displayParking();
    }
    @PutMapping("/updateParking")
    public void updateParking(@RequestBody Parking parking){
        parkService.updateParking(parking);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteParking(@PathVariable Long id) {
        parkService.deleteParking(id);
        return ResponseEntity.ok("Parking supprimé avec succès !");
    }
    @PostMapping("/parking/{parkingId}/etage")
    public ResponseEntity<String> addEtageToParking(@PathVariable Long parkingId, @RequestBody Etage etage) {
        try {
            parkService.addEtageToParking(parkingId, etage);
            return ResponseEntity.ok("Étage ajouté avec succès au parking.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEtage(@PathVariable("id") Long etageId, @RequestBody Etage etageDetails) {
        try {
            parkService.updateEtage(etageId, etageDetails);
            return ResponseEntity.ok("Étage mis à jour avec succès !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("La capacité totale des étages dépasse la capacité totale du parking.");}
    }














}

