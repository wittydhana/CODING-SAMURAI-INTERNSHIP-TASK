import java.io.*;
import java.util.*;

public class ECommerce {

    // Scanner for user input
    private static Scanner scanner = new Scanner(System.in);

    // Product list
    private static List<Product> products = new ArrayList<>();

    // Current logged-in user
    private static User currentUser = null;

    // User's shopping cart
    private static Cart cart = new Cart();

    public static void main(String[] args) {
        loadProducts();
        System.out.println("Welcome to Simple E-Commerce System");

        while (true) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                register();
            } else if (choice.equals("2")) {
                if (login()) {
                    userMenu();
                }
            } else if (choice.equals("3")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    // Load sample products
    private static void loadProducts() {
        products.add(new Product(1, "Laptop", 50000));
        products.add(new Product(2, "Phone", 15000));
        products.add(new Product(3, "Headphones", 2000));
    }

    // User registration
    private static void register() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));
            writer.write(username + "," + password);
            writer.newLine();
            writer.close();

            System.out.println("Registration successful!");
        } catch (IOException e) {
            System.out.println("Error saving user.");
        }
    }

    // User login
    private static boolean login() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            BufferedReader reader = new BufferedReader(new FileReader("users.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    currentUser = new User(username, password);
                    System.out.println("Login successful!");
                    reader.close();
                    return true;
                }
            }
            reader.close();
            System.out.println("Invalid credentials.");
        } catch (FileNotFoundException e) {
            System.out.println("No users registered yet.");
        } catch (IOException e) {
            System.out.println("Error reading users file.");
        }
        return false;
    }

    // User menu after login
    private static void userMenu() {
        while (true) {
            System.out.println("\nWelcome, " + currentUser.getUsername());
            System.out.println("1. Browse Products");
            System.out.println("2. View Cart");
            System.out.println("3. Checkout");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                browseProducts();
            } else if (choice.equals("2")) {
                viewCart();
            } else if (choice.equals("3")) {
                checkout();
            } else if (choice.equals("4")) {
                currentUser = null;
                cart = new Cart();
                System.out.println("Logged out.");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    // Display products and add to cart
    private static void browseProducts() {
        System.out.println("\nProducts:");
        for (Product p : products) {
            System.out.println(p.getId() + ". " + p.getName() + " - Rs." + p.getPrice());
        }
        System.out.print("Enter product ID to add to cart or 0 to go back: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (id == 0) return;

            for (Product p : products) {
                if (p.getId() == id) {
                    cart.addProduct(p);
                    System.out.println(p.getName() + " added to cart.");
                    return;
                }
            }
            System.out.println("Product not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    // Show items in cart
    private static void viewCart() {
        System.out.println("\nYour Cart:");
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        for (Product p : cart.getItems()) {
            System.out.println(p.getName() + " - Rs." + p.getPrice());
        }
        System.out.println("Total: Rs." + cart.getTotal());
    }

    // Checkout and save order
    private static void checkout() {
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("\nCheckout");
        viewCart();
        System.out.print("Confirm purchase? (yes/no): ");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("yes")) {
            saveOrder();
            cart = new Cart();
            System.out.println("Purchase successful!");
        } else {
            System.out.println("Checkout cancelled.");
        }
    }

    // Save order details in file
    private static void saveOrder() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("orders.txt", true));
            writer.write("User: " + currentUser.getUsername());
            writer.newLine();
            for (Product p : cart.getItems()) {
                writer.write(p.getName() + "," + p.getPrice());
                writer.newLine();
            }
            writer.write("Total: Rs." + cart.getTotal());
            writer.newLine();
            writer.write("----");
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving order.");
        }
    }
}

// User class
class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}

// Product class
class Product {
    private int id;
    private String name;
    private double price;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
}

// Cart class
class Cart {
    private List<Product> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public void addProduct(Product product) {
        items.add(product);
    }

    public List<Product> getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;
        for (Product p : items) {
            total += p.getPrice();
        }
        return total;
    }
}
