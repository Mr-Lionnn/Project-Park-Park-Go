import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Course {
    private static int nextCourseID = 1; // To auto-generate courseID
    private int idCourse;
    private Client client;
    private Vehicule vehicule;
    private Lieu lieuDepart;
    private Lieu lieuDarriver;
    private String timeOrdered;
    private String timeArrived;
    private double distance;
    private int amount; // Rounded price
    private int passengers;
    private boolean completed;

    public Course(int idCourse, Client client, Vehicule vehicule, Lieu lieuDepart, Lieu lieuDarriver, String timeOrdered, double distance, int amount, int passengers) {
        this.idCourse = idCourse;
        this.client = client;
        this.vehicule = vehicule;
        this.lieuDepart = lieuDepart;
        this.lieuDarriver = lieuDarriver;
        this.timeOrdered = timeOrdered;
        this.distance = distance;
        this.amount = amount;
        this.passengers = passengers;
        this.completed = false;
    }

    public static int generateUniqueID() {
        return nextCourseID++;
    }

    // Register the course in the course file
    public void enregistrerCourse() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("course.csv", true))) {
            writer.write(idCourse + "," + client.getNumero() + "," + client.getNom() + "," + client.getPrenom() + "," + timeOrdered + "," + lieuDepart.getLongitude() + "," + lieuDepart.getLatitude() + "," + lieuDarriver.getLongitude() + "," + lieuDarriver.getLatitude() + "," + distance + "," + amount + "," + passengers + "," + vehicule.getNumeroMatricule() + "," + vehicule.getType() + "," + completed);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generate a receipt for the course
    public void genererRecu() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        this.timeArrived = formatter.format(date);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Recu.csv", true))) {
            writer.write(idCourse + "," + client.getNumero() + "," + client.getNom() + "," + client.getPrenom() + "," + lieuDepart.getLongitude() + "," + lieuDepart.getLatitude() + "," + lieuDarriver.getLongitude() + "," + lieuDarriver.getLatitude() + "," + distance + "," + amount + "," + passengers + "," + vehicule.getNumeroMatricule() + "," + vehicule.getType() + "," + timeOrdered + "," + timeArrived + "," + getTotalTime());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all courses from the file
    public static List<Course> chargerCourses(List<Client> clients, List<Vehicule> vehicules, List<Lieu> lieux) {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("course.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 15) {
                    Client client = clients.stream().filter(c -> c.getNumero().equals(fields[1])).findFirst().orElse(null);
                    Vehicule vehicule = vehicules.stream().filter(v -> v.getNumeroMatricule().equals(fields[12])).findFirst().orElse(null);
                    Lieu lieuDepart = new Lieu("LieuDepart", Double.parseDouble(fields[5]), Double.parseDouble(fields[6]));
                    Lieu lieuDarriver = new Lieu("LieuDarriver", Double.parseDouble(fields[7]), Double.parseDouble(fields[8]));
                    if (client != null && vehicule != null && lieuDepart != null && lieuDarriver != null) {
                        Course course = new Course(Integer.parseInt(fields[0]), client, vehicule, lieuDepart, lieuDarriver, fields[4], Double.parseDouble(fields[9]), Integer.parseInt(fields[10]), Integer.parseInt(fields[11]));
                        course.completed = Boolean.parseBoolean(fields[14]); // Set completion status from file
                        courses.add(course);
                        if (course.idCourse >= nextCourseID) {
                            nextCourseID = course.idCourse + 1; // Update nextCourseID
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Display trip details
    public void afficherDetailsCourse() {
        System.out.println("Détails de la course:");
        System.out.println("Client ID: " + client.getNumero());
        System.out.println("Nom du client: " + client.getNom() + " " + client.getPrenom());
        System.out.println("Numéro du client: " + client.getNumero());
        System.out.println("Véhicule: " + vehicule.getNumeroMatricule() + ", Type: " + vehicule.getType());
        System.out.println("Lieu de départ: (" + lieuDepart.getLongitude() + ", " + lieuDepart.getLatitude() + ")");
        System.out.println("Lieu d'arrivée: (" + lieuDarriver.getLongitude() + ", " + lieuDarriver.getLatitude() + ")");
        System.out.println("Distance: " + distance + " km");
        System.out.println("Prix: " + amount + " FCFA");
        System.out.println("Temps de commande: " + timeOrdered);
        System.out.println("Passagers: " + passengers);
        System.out.println("Temps estimé d'arrivée: " + estimateArrivalTime());
    }

    // Display receipt details
    public void afficherRecu() {
        System.out.println("Reçu de la course:");
        System.out.println("Client ID: " + client.getNumero());
        System.out.println("Nom du client: " + client.getNom() + " " + client.getPrenom());
        System.out.println("Lieu de départ: (" + lieuDepart.getLongitude() + ", " + lieuDepart.getLatitude() + ")");
        System.out.println("Lieu d'arrivée: (" + lieuDarriver.getLongitude() + ", " + lieuDarriver.getLatitude() + ")");
        System.out.println("Distance: " + distance + " km");
        System.out.println("Prix: " + amount + " FCFA");
        System.out.println("Temps de commande: " + timeOrdered);
        System.out.println("Temps d'arrivée: " + timeArrived);
        System.out.println("Temps total de la course: " + getTotalTime());
    }

    // Terminate the course and generate a receipt
    public void terminerCourse() {
        completed = true;
        genererRecu();
    }

    // Calculate the total time for the ride
    private String getTotalTime() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date orderedDate = format.parse(timeOrdered);
            Date arrivedDate = format.parse(timeArrived);
            long diff = arrivedDate.getTime() - orderedDate.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000);
            return diffHours + " hours " + diffMinutes + " minutes " + diffSeconds + " seconds";
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    // Estimate arrival time (assuming an average speed of 60 km/h)
    private String estimateArrivalTime() {
        double averageSpeed = 60.0; // km/h
        double estimatedTime = distance / averageSpeed * 60; // in minutes
        return (int) estimatedTime + " minutes";
    }

    // Getter methods
    public int getIdCourse() {
        return idCourse;
    }

    public Client getClient() {
        return client;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public Lieu getLieuDepart() {
        return lieuDepart;
    }

    public Lieu getLieuDarriver() {
        return lieuDarriver;
    }

    public String getTimeOrdered() {
        return timeOrdered;
    }

    public double getDistance() {
        return distance;
    }

    public int getAmount() {
        return amount;
    }

    public int getPassengers() {
        return passengers;
    }

    public boolean isCompleted() {
        return completed;
    }
}
