import java.io.*;
import java.util.*;

public class GestionnaireCourse {
    private List<Vehicule> vehicules;

    public GestionnaireCourse(List<Vehicule> vehicules) {
        this.vehicules = vehicules;
    }

    // Register a new vehicle
    public void enregistrerVehicule(Vehicule vehicule) {
        vehicules.add(vehicule);
        Vehicule.saveVehicules(vehicules);
    }

    // Remove a vehicle by its number plate
    public void retirerVehicule(String numeroMatricule) {
        vehicules.removeIf(vehicule -> vehicule.getNumeroMatricule().equals(numeroMatricule));
        Vehicule.saveVehicules(vehicules);
    }

    // List all vehicles
    public void listerVehicules() {
        for (Vehicule vehicule : vehicules) {
            System.out.println("Vehicle: " + vehicule.getNumeroMatricule() + ", Type: " + vehicule.getType() + ", Car ID: " + vehicule.getCarID() + ", Etat: " + vehicule.getEtat());
        }
    }
}
