import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client currentUser = null;
        boolean isAdmin = false;

        System.out.println("Welcome to the Autonomous Vehicle Rental Application");

        // User registration and login loop
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            int choice = Utils.getIntInputWithRetries(scanner, "Enter option: ", 3);

            switch (choice) {
                case 1:
                    // Registration process
                    String numero = Utils.getInputWithRetries(scanner, "Enter phone number: ", 3);
                    String nom = Utils.getInputWithRetries(scanner, "Enter last name: ", 3);
                    String prenom = Utils.getInputWithRetries(scanner, "Enter first name: ", 3);
                    String password = Utils.getInputWithRetries(scanner, "Enter password: ", 3);
                    Client client = new Client(numero, nom, prenom, password);
                    if (client.creerCompte() == 0) {
                        System.out.println("Registration successful! Your client ID is: " + client.getClientID());
                    } else {
                        System.out.println("Registration failed. Ensure the phone number is exactly 13 digits.");
                    }
                    break;
                case 2:
                    // Login process
                    String loginNumero = Utils.getInputWithRetries(scanner, "Enter phone number: ", 3);
                    String loginPassword = Utils.getInputWithRetries(scanner, "Enter password: ", 3);
                    if (loginNumero.equals("admin") && loginPassword.equals("admin")) {
                        isAdmin = true;
                        currentUser = null; // Clear currentUser to indicate admin login
                        System.out.println("Admin login successful! Welcome, Admin.");
                        break;
                    } else {
                        currentUser = Client.login(loginNumero, loginPassword);
                        if (currentUser == null) {
                            System.out.println("Login failed. Please try again.");
                        } else {
                            System.out.println("Login successful! Welcome, " + currentUser.getPrenom());
                        }
                    }
                    break;
                default:
                    System.out.println("Invalid choice");
            }

            if (currentUser != null || isAdmin) {
                break;
            }
        }

        // Load existing vehicles, locations, and courses
        List<Vehicule> vehicules = Vehicule.chargerVehicules();
        List<Lieu> lieux = new ArrayList<>();
        List<Course> courses = Course.chargerCourses(Client.chargerClients(), vehicules, lieux);
        GestionnaireCourse gestionnaireCourse = new GestionnaireCourse(vehicules);

        // Main menu loop
        while (true) {
            if (isAdmin) {
                adminMenu(scanner, vehicules, courses, gestionnaireCourse);
            } else {
                clientMenu(scanner, currentUser, vehicules, lieux, courses, gestionnaireCourse);
            }
        }
    }

    // Admin menu to manage the system
    private static void adminMenu(Scanner scanner, List<Vehicule> vehicules, List<Course> courses, GestionnaireCourse gestionnaireCourse) {
        System.out.println("1. Add Vehicle");
        System.out.println("2. List All Clients");
        System.out.println("3. List All Rides");
        System.out.println("4. List All Vehicles"); // New option for listing vehicles
        System.out.println("5. Remove Vehicle");
        System.out.println("6. Remove Client");
        System.out.println("7. Exit");

        int choice = Utils.getIntInputWithRetries(scanner, "Enter option: ", 3);

        switch (choice) {
            case 1:
                // Add a new vehicle
                String numeroMatricule = Utils.getInputWithRetries(scanner, "Enter vehicle number plate: ", 3);
                String type = Utils.getInputWithRetries(scanner, "Enter vehicle type: ", 3);
                String carID = Utils.getInputWithRetries(scanner, "Enter car ID: ", 3);
                int etat = Utils.getIntInputWithRetries(scanner, "Enter vehicle state: ", 3);
                Vehicule vehicule = new Vehicule(numeroMatricule, type, carID, etat);
                gestionnaireCourse.enregistrerVehicule(vehicule);
                break;
            case 2:
                // List all clients
                List<Client> clients = Client.chargerClients();
                System.out.println("All Clients:");
                for (Client client : clients) {
                    System.out.println("Client ID: " + client.getClientID() + ", Name: " + client.getNom() + " " + client.getPrenom() + ", Phone: " + client.getNumero());
                }
                break;
            case 3:
                // List all rides
                System.out.println("All Rides:");
                listAllRides();
                break;
            case 4:
                // List all vehicles
                System.out.println("All Vehicles:");
                gestionnaireCourse.listerVehicules();
                break;
            case 5:
                // Remove a vehicle
                String vehicleToRemove = Utils.getInputWithRetries(scanner, "Enter vehicle number plate to remove: ", 3);
                gestionnaireCourse.retirerVehicule(vehicleToRemove);
                break;
            case 6:
                // Remove a client
                String clientToRemove = Utils.getInputWithRetries(scanner, "Enter client phone number to remove: ", 3);
                Client.supprimerClient(clientToRemove);
                break;
            case 7:
                // Exit the application
                System.out.println("Exiting application");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice");
        }
    }

    // Client menu for ordering rides, listing rides, and updating information
    private static void clientMenu(Scanner scanner, Client currentUser, List<Vehicule> vehicules, List<Lieu> lieux, List<Course> courses, GestionnaireCourse gestionnaireCourse) {
        System.out.println("1. Order Ride");
        System.out.println("2. List My Rides");
        System.out.println("3. Terminate Active Ride");
        System.out.println("4. Update Phone number");
        System.out.println("5. Update Password");
        System.out.println("6. Exit");

        int choice = Utils.getIntInputWithRetries(scanner, "Enter option: ", 3);

        switch (choice) {
            case 1:
                // Order a ride
                if (checkAndCompleteActiveRide(scanner, currentUser, courses)) {
                    break;
                }

                int passengers = Utils.getIntInputWithRetries(scanner, "Enter number of passengers: ", 3);
                double longitudeDepart = Utils.getDoubleInputWithRetries(scanner, "Enter lieuDepart longitude: ", 3);
                double latitudeDepart = Utils.getDoubleInputWithRetries(scanner, "Enter lieuDepart latitude: ", 3);
                double longitudeArriver = Utils.getDoubleInputWithRetries(scanner, "Enter lieuDarriver longitude: ", 3);
                double latitudeArriver = Utils.getDoubleInputWithRetries(scanner, "Enter lieuDarriver latitude: ", 3);

                Lieu lieuDepart = new Lieu("LieuDepart", longitudeDepart, latitudeDepart);
                Lieu lieuDarriver = new Lieu("LieuDarriver", longitudeArriver, latitudeArriver);

                double distance = Lieu.calculateDistance(lieuDepart, lieuDarriver);
                int price = (int) Math.ceil(distance * 150); // Round up the price

                Vehicule availableVehicule = vehicules.stream()
                        .filter(v -> v.getEtat() + passengers <= 5)
                        .findFirst()
                        .orElse(null);

                if (availableVehicule != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String timeOrdered = formatter.format(date);
                    System.out.println("Vehicle available for your ride:");
                    System.out.println("Vehicle: " + availableVehicule.getNumeroMatricule() + ", Type: " + availableVehicule.getType() + ", Price: " + price + " FCFA");
                    String acceptRide = Utils.getInputWithRetries(scanner, "Do you accept the ride? (yes/no): ", 3);
                    if ("yes".equalsIgnoreCase(acceptRide)) {
                        Course course = new Course(currentUser, availableVehicule, lieuDepart, lieuDarriver, timeOrdered, distance, price, passengers);
                        course.enregistrerCourse();
                        courses.add(course);
                        availableVehicule.setEtat(availableVehicule.getEtat() + passengers);  // Update the vehicle state to indicate it is in use
                        System.out.println("Your ride is on the way.");
                        course.effectuerCourse();
                    } else {
                        System.out.println("Ride not accepted.");
                    }
                } else {
                    System.out.println("No available vehicles at the moment.");
                }
                break;
            case 2:
                // List the client's rides
                listClientRides(currentUser.getNumero());
                break;
            case 3:
                // Terminate an active ride
                terminateActiveRide(scanner, currentUser, courses);
                break;
            case 4:
                // Update the client's phone number
                String newNumero = Utils.getInputWithRetries(scanner, "Enter new phone number: ", 3);
                currentUser.updateNumero(newNumero);
                System.out.println("Phone number updated successfully.");
                break;
            case 5:
                // Update the client's password
                String newPassword = Utils.getInputWithRetries(scanner, "Enter new password: ", 3);
                currentUser.updatePassword(newPassword);
                System.out.println("Password updated successfully.");
                break;
            case 6:
                // Exit the application
                System.out.println("Exiting application");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice");
        }
    }

    // Check and complete active ride if the client has one
    private static boolean checkAndCompleteActiveRide(Scanner scanner, Client currentUser, List<Course> courses) {
        Course activeCourse = courses.stream().filter(c -> c.getClient().equals(currentUser) && !c.isCompleted()).findFirst().orElse(null);
        if (activeCourse != null) {
            System.out.print("You have an active ride. Is it completed? (yes/no): ");
            String isCompleted = scanner.nextLine();
            if ("yes".equalsIgnoreCase(isCompleted)) {
                activeCourse.terminerCourse();
                System.out.println("Your previous ride has been completed.");
                activeCourse.genererRecu();
                courses.remove(activeCourse);
                activeCourse.getVehicule().setEtat(activeCourse.getVehicule().getEtat() - activeCourse.getPassengers());
                return false;
            } else {
                System.out.println("Please complete your current ride before ordering a new one.");
                return true;
            }
        }
        return false;
    }

    // Terminate an active ride
    private static void terminateActiveRide(Scanner scanner, Client currentUser, List<Course> courses) {
        Course activeCourse = courses.stream().filter(c -> c.getClient().equals(currentUser) && !c.isCompleted()).findFirst().orElse(null);
        if (activeCourse != null) {
            activeCourse.terminerCourse();
            System.out.println("Your active ride has been terminated.");
            activeCourse.genererRecu();
            courses.remove(activeCourse);
            activeCourse.getVehicule().setEtat(activeCourse.getVehicule().getEtat() - activeCourse.getPassengers());
        } else {
            System.out.println("You don't have any active rides.");
        }
    }

    // List the client's rides from the receipt file
    private static void listClientRides(String clientNumero) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Recu.csv"))) {
            String line;
            boolean hasRides = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains(clientNumero)) {
                    System.out.println(line);
                    hasRides = true;
                }
            }
            if (!hasRides) {
                System.out.println("You have no completed rides.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // List all rides from the receipt file for admin
    private static void listAllRides() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Recu.csv"))) {
            String line;
            boolean hasRides = false;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                hasRides = true;
            }
            if (!hasRides) {
                System.out.println("There are no completed rides.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
