package org.example;

import jakarta.persistence.*;

public class App {
     static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("library_system");

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            User user = new User();

            user.setUsername("testUser");
            user.setEmail("testMail");
            user.setPassword("test");
            user.setFirstName("test");
            user.setLastName("test");

            em.persist(user);


            em.getTransaction().commit();

        } finally {
            em.close();
            emf.close();
        }
    }
}

