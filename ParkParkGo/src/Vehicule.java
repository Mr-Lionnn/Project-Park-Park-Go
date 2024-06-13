import java.io.*;
import java.util.*;

public class Vehicule {
    private String numeroMatricule;
    private String type;
    private String carID;
    private int etat;

    public Vehicule(String numeroMatricule, String type, String carID, int etat) {
        this.numeroMatricule = numeroMatricule;
        this.type = type;
        this.carID = carID;
        this.etat = etat;
    }

    // Getter and Setter methods
    public String getNumeroMatricule() {
        return numeroMatricule;
    }

    public String getType() {
        return type;
    }

    public String getCarID() {
        return carID;
    }

    public int getEtat() {
        return etat;
    }

    public void setNumeroMatricule(String numeroMatricule) {
        this.numeroMatricule = numeroMatricule;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }

    // Load all vehicles from the file
    public static List<Vehicule> chargerVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("voiture.csv"))) { // Changed to voiture.csv
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 4) {
                    Vehicule vehicule = new Vehicule(fields[0], fields[1], fields[2], Integer.parseInt(fields[3]));
                    vehicules.add(vehicule);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vehicules;
    }

    // Save all vehicles to the file
    public static void saveVehicules(List<Vehicule> vehicules) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("voiture.csv"))) { // Changed to voiture.csv
            for (Vehicule vehicule : vehicules) {
                writer.write(vehicule.getNumeroMatricule() + "," + vehicule.getType() + "," + vehicule.getCarID() + "," + vehicule.getEtat());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
