import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        LoginManager loginService = new LoginManager();

        Scanner sc = new Scanner(System.in);
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        boolean success = loginService.login(username, password);
        if (success) {
            System.out.println("Login avvenuto con successo!");
        } else {
            System.out.println("Credenziali errate.");
        }
    }
}