package de.tobiasblaschke.eventsource.sample.domain;

import java.util.Objects;

public class User {
    private final int userId;
    private final String givenName;
    private final String surname;
    private final String email;

    public User(int userId, String givenName, String surname, String email) {
        this.userId = userId;
        this.givenName = givenName;
        this.surname = surname;
        this.email = email;
    }

    public User(final User copyFrom) {
        this.userId = copyFrom.getUserId();
        this.givenName = copyFrom.getGivenName();
        this.surname = copyFrom.getSurname();
        this.email = copyFrom.getEmail();
    }

    public int getUserId() {
        return userId;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId &&
                Objects.equals(givenName, user.givenName) &&
                Objects.equals(surname, user.surname) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, givenName, surname, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
