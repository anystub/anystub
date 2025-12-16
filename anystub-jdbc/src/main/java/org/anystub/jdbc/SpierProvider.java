package org.anystub.jdbc;

public class SpierProvider {

    private SpierProvider() {
    }

    private static Spier spier = null;

    public static Spier getSpier() {
        return spier;
    }

    public static void setSpier(Spier spier) {
        SpierProvider.spier = spier;
    }
}
