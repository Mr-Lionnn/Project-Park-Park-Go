import java.io.*;
import java.util.*;

public class Course {
    private static int nextCourseID = 1; // To auto-generate courseID
    private int idCourse;
    private Client client;
    private Vehicule vehicule;
    private Lieu lieuDepart;
    private Lieu lieuDarriver;
    private String timeOrdered;
    private double distance;
    private int amount; // Rounded price
    private int passengers;
    private boolean completed;

    public Course(Client client, Vehicule vehicule, Lieu lieuDepart, Lieu lieuDarriver, String timeOrdered, double distance, int amount, int passengers) {
        this.idCourse = nextCourseID++;
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

    // Register the course in the course file
    public void enregistrerCourse() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("course.csv", true))) {
            writer.write(idCourse + "," + client.getNumero() + "," + client.getNom() + "," + client.getPrenom() + "," + timeOrdered + "," + lieuDepart.getNom() + "," + lieuDarriver.getNom() + "," + distance + "," + amount + "," + passengers + "," + vehicule.getNumeroMatricule() + "," + vehicule.getType() + "," + completed);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generate a receipt for the course
    public void genererRecu() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Recu.csv", true))) {
            writer.write("Course ID: " + idCourse + ", Client ID: " + client.getNumero() + ", Client: " + client.getNom() + " " + client.getPrenom() + ", Client Number: " + client.getNumero() + ", From: (" + lieuDepart.getLongitude() + ", " + lieuDepart.getLatitude() + ") To: (" + lieuDarriver.getLongitude() + ", " + lieuDarriver.getLatitude() + "), Distance: " + distance + " km, Price: " + amount + " FCFA, Time Ordered: " + timeOrdered + ", Vehicle: " + vehicule.getNumeroMatricule() + ", Type: " + vehicule.getType());
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
                if (fields.length == 13) {
                    Client client = clients.stream().filter(c -> c.getNumero().equals(fields[1])).findFirst().orElse(null);
                    Vehicule vehicule = vehicules.stream().filter(v -> v.getNumeroMatricule().equals(fields[10])).findFirst().orElse(null);
                    Lieu lieuDepart = lieux.stream().filter(l -> l.getNom().equals(fields[5])).findFirst().orElse(null);
                    Lieu lieuDarriver = lieux.stream().filter(l -> l.getNom().equals(fields[6])).findFirst().orElse(null);
                    if (client != null && vehicule != null && lieuDepart != null && lieuDarriver != null) {
                        Course course = new Course(client, vehicule, lieuDepart, lieuDarriver, fields[4], Double.parseDouble(fields[7]), Integer.parseInt(fields[8]), Integer.parseInt(fields[9]));
                        course.idCourse = Integer.parseInt(fields[0]); // Set course ID from file
                        course.completed = Boolean.parseBoolean(fields[12]); // Set completion status from file
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
    public void effectuerCourse() {
        System.out.println("Trip Details:");
        System.out.println("Client ID: " + client.getNumero());
        System.out.println("Client name: " + client.getNom() + " " + client.getPrenom());
        System.out.println("Client number: " + client.getNumero());
        System.out.println("Vehicle: " + vehicule.getNumeroMatricule() + ", Type: " + vehicule.getType());
        System.out.println("LieuDepart: (" + lieuDepart.getLongitude() + ", " + lieuDepart.getLatitude() + ")");
        System.out.println("LieuDarriver: (" + lieuDarriver.getLongitude() + ", " + lieuDarriver.getLatitude() + ")");
        System.out.println("Distance: " + distance + " km");
        System.out.println("Price: " + amount + " FCFA");
        genererRecu();
    }

    // Terminate the course and generate a receipt
    public void terminerCourse() {
        completed = true;
        enregistrerCourse(); // Update the course file to mark it as completed
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
