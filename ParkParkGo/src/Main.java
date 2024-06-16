import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Client currentUser = null;
        boolean isAdmin = false;

        System.out.println("Bienvenue chez MONTO. Mi Kwaboooo");

        // Ensure headers are written to CSV files if they don't exist
        Utils.ensureCsvHeaders("clients.csv", "clientID,numero,nom,prenom,password");
        Utils.ensureCsvHeaders("course.csv", "idCourse,numeroClient,nomClient,prenomClient,timeOrdered,longitudeDepart,latitudeDepart,longitudeArriver,latitudeArriver,distance,amount,passengers,numeroVehicule,typeVehicule,completed");
        Utils.ensureCsvHeaders("Recu.csv", "idCourse,numeroClient,nomClient,prenomClient,longitudeDepart,latitudeDepart,longitudeArriver,latitudeArriver,distance,amount,passengers,numeroVehicule,typeVehicule,timeOrdered,timeArrived,totalTime");
        Utils.ensureCsvHeaders("voiture.csv", "carID,numeroMatricule,type");

        // User registration and login loop
        while (true) {
            System.out.println("1. S'inscrire");
            System.out.println("2. Se connecter");
            int choice = Utils.getIntInputWithRetries(scanner, "Entrez l'option: ", 3);

            switch (choice) {
                case 1:
                    // Registration process
                    String numero = Utils.getInputWithRetries(scanner, "Entrez le numéro de téléphone: ", 3);
                    String nom = Utils.getInputWithRetries(scanner, "Entrez le nom: ", 3);
                    String prenom = Utils.getInputWithRetries(scanner, "Entrez le prénom: ", 3);
                    String password = Utils.getInputWithRetries(scanner, "Entrez le mot de passe: ", 3);
                    Client client = new Client(Client.generateUniqueID(), numero, nom, prenom, password);
                    if (client.creerCompte() == 0) {
                        System.out.println("Inscription réussie ! Votre identifiant client est: " + client.getClientID());
                    } else {
                        System.out.println("Échec de l'inscription. Assurez-vous que le numéro de téléphone contient exactement 8 chiffres.");
                    }
                    break;
                case 2:
                    // Login process
                    String loginNumero = Utils.getInputWithRetries(scanner, "Entrez le numéro de téléphone: ", 3);
                    String loginPassword = Utils.getInputWithRetries(scanner, "Entrez le mot de passe: ", 3);
                    if (loginNumero.equals("admin") && loginPassword.equals("admin")) {
                        isAdmin = true;
                        currentUser = null; // Clear currentUser to indicate admin login
                        System.out.println("Connexion administrateur réussie ! Bienvenue, Admin.");
                        break;
                    } else {
                        currentUser = Client.login(loginNumero, loginPassword);
                        if (currentUser == null) {
                            System.out.println("Échec de la connexion. Veuillez réessayer.");
                        } else {
                            System.out.println("Connexion réussie ! Bienvenue, " + currentUser.getPrenom());
                        }
                    }
                    break;
                default:
                    System.out.println("Choix invalide");
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
        System.out.println("1. Ajouter un véhicule");
        System.out.println("2. Lister tous les clients");
        System.out.println("3. Lister toutes les courses");
        System.out.println("4. Lister tous les véhicules");
        System.out.println("5. Supprimer un véhicule");
        System.out.println("6. Supprimer un client");
        System.out.println("7. Quitter");

        int choice = Utils.getIntInputWithRetries(scanner, "Entrez l'option: ", 3);

        switch (choice) {
            case 1:
                // Add a new vehicle
                String numeroMatricule = Utils.getInputWithRetries(scanner, "Entrez le numéro de plaque du véhicule: ", 3);
                String type = Utils.getInputWithRetries(scanner, "Entrez le type de véhicule: ", 3);
                Vehicule vehicule = new Vehicule(Vehicule.generateUniqueID(), numeroMatricule, type);
                gestionnaireCourse.enregistrerVehicule(vehicule);
                break;
            case 2:
                // List all clients
                List<Client> clients = Client.chargerClients();
                System.out.println("Tous les clients:");
                for (Client client : clients) {
                    System.out.println("Client ID: " + client.getClientID() + ", Nom: " + client.getNom() + " " + client.getPrenom() + ", Téléphone: " + client.getNumero());
                }
                break;
            case 3:
                // List all rides
                System.out.println("Toutes les courses:");
                listAllRides();
                break;
            case 4:
                // List all vehicles
                System.out.println("Tous les véhicules:");
                gestionnaireCourse.listerVehicules();
                break;
            case 5:
                // Remove a vehicle
                String vehicleToRemove = Utils.getInputWithRetries(scanner, "Entrez le numéro de plaque du véhicule à supprimer: ", 3);
                gestionnaireCourse.retirerVehicule(vehicleToRemove);
                break;
            case 6:
                // Remove a client
                String clientToRemove = Utils.getInputWithRetries(scanner, "Entrez le numéro de téléphone du client à supprimer: ", 3);
                Client.supprimerClient(clientToRemove);
                break;
            case 7:
                // Exit the application
                System.out.println("Quitter l'application");
                System.exit(0);
                break;
            default:
                System.out.println("Choix invalide");
        }
    }

    // Client menu for ordering rides, listing rides, and updating information
    private static void clientMenu(Scanner scanner, Client currentUser, List<Vehicule> vehicules, List<Lieu> lieux, List<Course> courses, GestionnaireCourse gestionnaireCourse) {
        System.out.println("1. Commander une course");
        System.out.println("2. Lister mes courses");
        System.out.println("3. Terminer une course active");
        System.out.println("4. Modifier les informations du compte");
        System.out.println("5. Quitter");

        int choice = Utils.getIntInputWithRetries(scanner, "Entrez l'option: ", 3);

        switch (choice) {
            case 1:
                // Order a ride
                if (checkAndCompleteActiveRide(scanner, currentUser, courses)) {
                    break;
                }

                int passengers = Utils.getIntInputWithRetries(scanner, "Entrez le nombre de passagers: ", 3);
                double longitudeDepart = Utils.getDoubleInputWithRetries(scanner, "Entrez la longitude de départ: ", 3);
                double latitudeDepart = Utils.getDoubleInputWithRetries(scanner, "Entrez la latitude de départ: ", 3);
                double longitudeArriver = Utils.getDoubleInputWithRetries(scanner, "Entrez la longitude d'arrivée: ", 3);
                double latitudeArriver = Utils.getDoubleInputWithRetries(scanner, "Entrez la latitude d'arrivée: ", 3);

                Lieu lieuDepart = new Lieu("LieuDepart", longitudeDepart, latitudeDepart);
                Lieu lieuDarriver = new Lieu("LieuDarriver", longitudeArriver, latitudeArriver);

                double distance = Lieu.calculateDistance(lieuDepart, lieuDarriver);
                int price = (int) Math.ceil(distance * 150); // Round up the price

                Vehicule availableVehicule = vehicules.stream()
                        .filter(v -> getPassengersInVehicle(v, courses) + passengers <= 5)
                        .findFirst()
                        .orElse(null);

                if (availableVehicule != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String timeOrdered = formatter.format(date);
                    System.out.println("Véhicule disponible pour votre course:");
                    System.out.println("Véhicule: " + availableVehicule.getNumeroMatricule() + ", Type: " + availableVehicule.getType() + ", Prix: " + price + " FCFA");
                    String acceptRide = Utils.getInputWithRetries(scanner, "Acceptez-vous la course? (oui/non): ", 3);
                    if ("oui".equalsIgnoreCase(acceptRide)) {
                        Course course = new Course(Course.generateUniqueID(), currentUser, availableVehicule, lieuDepart, lieuDarriver, timeOrdered, distance, price, passengers);
                        course.enregistrerCourse();
                        courses.add(course);
                        System.out.println("Votre course est en route.");
                        course.afficherDetailsCourse();
                    } else {
                        System.out.println("Course non acceptée.");
                    }
                } else {
                    System.out.println("Aucun véhicule disponible pour le moment.");
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
                // Modify account information
                modifyAccountInformation(scanner, currentUser);
                break;
            case 5:
                // Exit the application
                System.out.println("Quitter l'application");
                System.exit(0);
                break;
            default:
                System.out.println("Choix invalide");
        }
    }

    // Check and complete active ride if the client has one
    private static boolean checkAndCompleteActiveRide(Scanner scanner, Client currentUser, List<Course> courses) {
        Course activeCourse = courses.stream().filter(c -> c.getClient().equals(currentUser) && !c.isCompleted()).findFirst().orElse(null);
        if (activeCourse != null) {
            System.out.print("Vous avez une course active. Est-elle terminée? (oui/non): ");
            String isCompleted = scanner.nextLine();
            if ("oui".equalsIgnoreCase(isCompleted)) {
                activeCourse.terminerCourse();
                System.out.println("Votre précédente course a été complétée.");
                activeCourse.genererRecu();
                activeCourse.afficherRecu(); // Show receipt in console
                courses.remove(activeCourse);
                return false;
            } else {
                System.out.println("Veuillez terminer votre course actuelle avant d'en commander une nouvelle.");
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
            System.out.println("Votre course active a été terminée.");
            activeCourse.genererRecu();
            activeCourse.afficherRecu(); // Show receipt in console
            courses.remove(activeCourse);
        } else {
            System.out.println("Vous n'avez aucune course active.");
        }
    }

    // Modify account information
    private static void modifyAccountInformation(Scanner scanner, Client currentUser) {
        System.out.println("1. Modifier le numéro de téléphone");
        System.out.println("2. Modifier le mot de passe");
        System.out.println("3. Modifier le nom");

        int choice = Utils.getIntInputWithRetries(scanner, "Entrez l'option: ", 3);

        switch (choice) {
            case 1:
                // Update the client's phone number
                String newNumero = Utils.getInputWithRetries(scanner, "Entrez le nouveau numéro de téléphone: ", 3);
                currentUser.updateNumero(newNumero);
                System.out.println("Numéro de téléphone mis à jour avec succès.");
                break;
            case 2:
                // Update the client's password
                String newPassword = Utils.getInputWithRetries(scanner, "Entrez le nouveau mot de passe: ", 3);
                currentUser.updatePassword(newPassword);
                System.out.println("Mot de passe mis à jour avec succès.");
                break;
            case 3:
                // Update the client's name
                String newNom = Utils.getInputWithRetries(scanner, "Entrez le nouveau nom: ", 3);
                String newPrenom = Utils.getInputWithRetries(scanner, "Entrez le nouveau prénom: ", 3);
                currentUser.updateNom(newNom, newPrenom);
                System.out.println("Nom mis à jour avec succès.");
                break;
            default:
                System.out.println("Choix invalide");
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
                System.out.println("Vous n'avez aucune course terminée.");
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
                System.out.println("Il n'y a aucune course terminée.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the number of passengers currently in the vehicle
    private static int getPassengersInVehicle(Vehicule vehicule, List<Course> courses) {
        int passengers = 0;
        for (Course course : courses) {
            if (course.getVehicule().equals(vehicule) && !course.isCompleted()) {
                passengers += course.getPassengers();
            }
        }
        return passengers;
    }
}
