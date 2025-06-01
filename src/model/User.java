package model;

import java.time.LocalDate;
import java.util.Objects;

public class User {
    private String username;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String cellphone;
    private String email;
    private String password;
    private Role role;

    public User() {
    }

    public User(String username, String name, String surname, LocalDate dateOfBirth,
                String cellphone, String email, String password, Role role) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.cellphone = cellphone;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getCellphone() { return cellphone; }
    public void setCellphone(String cellphone) { this.cellphone = cellphone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public void validate() {
        Objects.requireNonNull(username, "username non può essere null");
        Objects.requireNonNull(name, "name non può essere null");
        Objects.requireNonNull(surname, "surname non può essere null");
        Objects.requireNonNull(dateOfBirth, "dateOfBirth non può essere null");
        Objects.requireNonNull(email, "email non può essere null");
        Objects.requireNonNull(password, "password non può essere null");
        Objects.requireNonNull(role, "role non può essere null");
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role=" + role +
                '}';
    }
}