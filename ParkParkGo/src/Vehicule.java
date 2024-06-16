import java.io.*;
import java.util.*;

public class Vehicule {
    private static int nextVehiculeID = 1;
    private String carID;
    private String numeroMatricule;
    private String type;

    public Vehicule(String carID, String numeroMatricule, String type) {
        this.carID = carID;
        this.numeroMatricule = numeroMatricule;
        this.type = type;
    }

    public static String generateUniqueID() {
        return "V" + (nextVehiculeID++);
    }

    // Getter and Setter methods
    public String getCarID() {
        return carID;
    }

    public String getNumeroMatricule() {
        return numeroMatricule;
    }

    public String getType() {
        return type;
    }

    public void setNumeroMatricule(String numeroMatricule) {
        this.numeroMatricule = numeroMatricule;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Load all vehicles from the file
    public static List<Vehicule> chargerVehicules() {
        List<Vehicule> vehicules = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("voiture.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 3) {
                    Vehicule vehicule = new Vehicule(fields[0], fields[1], fields[2]);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("voiture.csv"))) {
            for (Vehicule vehicule : vehicules) {
                writer.write(vehicule.getCarID() + "," + vehicule.getNumeroMatricule() + "," + vehicule.getType());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
