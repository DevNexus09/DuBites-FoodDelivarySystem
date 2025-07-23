package com.foodordersystem.database;

import com.foodordersystem.model.entities.Restaurant;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDatabase {
    private static final String RESTAURANT_FILE = "restaurants.dat";
    private List<Restaurant> restaurants;

    public RestaurantDatabase() {
        restaurants = new ArrayList<>();
        loadRestaurants();
    }

    @SuppressWarnings("unchecked")
    private void loadRestaurants() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RESTAURANT_FILE))) {
            restaurants = (List<Restaurant>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, which is fine on first run.
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveRestaurants() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESTAURANT_FILE))) {
            oos.writeObject(restaurants);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new restaurant or updates an existing one.
     * A restaurant is uniquely identified by the combination of its name and owner.
     *
     * @param restaurant The restaurant to add or update.
     */
    public void addRestaurant(Restaurant restaurant) {
        // FIX: Identify the restaurant by its name AND owner to allow for updates
        // without deleting other restaurants from the same owner.
        restaurants.removeIf(r -> r.getOwnerUsername().equals(restaurant.getOwnerUsername())
                && r.getName().equals(restaurant.getName()));
        restaurants.add(restaurant);
        saveRestaurants();
    }

    public Restaurant findRestaurantByOwner(String ownerUsername) {
        for (Restaurant r : restaurants) {
            if (r.getOwnerUsername().equals(ownerUsername)) {
                return r;
            }
        }
        return null;
    }

    public Restaurant findRestaurantByName(String name) {
        for (Restaurant r : restaurants) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurants;
    }
}