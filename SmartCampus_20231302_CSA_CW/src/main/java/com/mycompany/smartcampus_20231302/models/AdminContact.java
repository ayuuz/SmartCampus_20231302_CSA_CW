package com.mycompany.smartcampus_20231302.models;

/**
 * Administrator contact details included in discovery response.
 */
public class AdminContact {

    private String name;
    private String email;

    /**
     * Creates an empty admin contact object.
     */
    public AdminContact() {
    }

    /**
     * Creates a populated admin contact object.
     *
     * @param name admin name
     * @param email admin email
     */
    public AdminContact(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * @return admin name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name admin name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return admin email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email admin email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
