import java.io.*;
import java.util.*;

public class Client {
    private static int nextClientID = 1;
    private String clientID;
    private String numero;
    private String nom;
    private String prenom;
    private String password;

    public Client(String clientID, String numero, String nom, String prenom, String password) {
        this.clientID = clientID;
        this.numero = numero;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
    }

    public static String generateUniqueID() {
        return "C" + (nextClientID++);
    }

    // Getter and Setter methods
    public String getClientID() {
        return clientID;
    }

    public String getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getPassword() {
        return password;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    // Create a new client account
    public int creerCompte() {
        if (numero.length() != 8 || !numero.matches("\\d+") || numeroAlreadyExists(numero)) {
            return -1; // invalid phone number or number already exists
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clients.csv", true))) {
            writer.write(clientID + "," + numero + "," + nom + "," + prenom + "," + password);
            writer.newLine();
            return 0; // success
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // failure
        }
    }

    private boolean numeroAlreadyExists(String numero) {
        List<Client> clients = chargerClients();
        for (Client client : clients) {
            if (client.getNumero().equals(numero)) {
                return true;
            }
        }
        return false;
    }

    // Load all clients from the file
    public static List<Client> chargerClients() {
        List<Client> clients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("clients.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 5) {
                    Client client = new Client(fields[0], fields[1], fields[2], fields[3], fields[4]);
                    clients.add(client);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // Client login method
    public static Client login(String numero, String password) {
        List<Client> clients = chargerClients();
        for (Client client : clients) {
            if (client.getNumero().equals(numero) && client.getPassword().equals(password)) {
                return client;
            }
        }
        return null;
    }

    // Update the client's phone number
    public void updateNumero(String newNumero) {
        if (newNumero.length() != 8 || !newNumero.matches("\\d+")) {
            System.out.println("Numéro de téléphone invalide. Il doit contenir exactement 8 chiffres.");
            return;
        }
        this.numero = newNumero;
        updateClientDetails();
    }

    // Update the client's password
    public void updatePassword(String newPassword) {
        this.password = newPassword;
        updateClientDetails();
    }

    // Update the client's name
    public void updateNom(String newNom, String newPrenom) {
        this.nom = newNom;
        this.prenom = newPrenom;
        updateClientDetails();
    }

    // Update client details in the file
    private void updateClientDetails() {
        List<Client> clients = chargerClients();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clients.csv"))) {
            for (Client client : clients) {
                if (client.getClientID().equals(this.clientID)) {
                    writer.write(clientID + "," + numero + "," + nom + "," + prenom + "," + password);
                } else {
                    writer.write(client.clientID + "," + client.numero + "," + client.nom + "," + client.prenom + "," + client.password);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Remove a client from the file
    public static void supprimerClient(String clientNumero) {
        List<Client> clients = chargerClients();
        clients.removeIf(client -> client.getNumero().equals(clientNumero));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("clients.csv"))) {
            for (Client client : clients) {
                writer.write(client.clientID + "," + client.numero + "," + client.nom + "," + client.prenom + "," + client.password);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
