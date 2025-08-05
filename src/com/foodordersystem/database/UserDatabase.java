package com.foodordersystem.database;

import com.foodordersystem.model.entities.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase {
    private static final String USER_FILE = "users.dat";
    private List<User> users;

    public UserDatabase() {
        users = new ArrayList<>();
        loadUsers();
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            users = (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public User findUser(String username, String password, String role) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password) && user.getRole().equals(role)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean isUsernameTaken(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}