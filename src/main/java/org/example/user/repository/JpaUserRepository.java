package org.example.user.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.EMFactory;
import org.example.LoanServices;
import org.example.User;

import java.util.List;

public class JpaUserRepository implements UserRepository {

    @Override
    public User createUser(User user) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Kunde inte skapa användare. E-post eller användarnamn är troligen redan upptaget.");
        }
    }

    @Override
    public User updateUser(Long id, String firstName, String lastName, String email, String password) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            em.getTransaction().begin();

            // Get user
            User user = em.find(User.class, id);

            if (user == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Kunde inte hitta användare med id: " + id);
            }

            // 3. Update user
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);

            em.getTransaction().commit();
            return user;
        } catch (Exception e) {
            throw new IllegalArgumentException("Kunde inte uppdatera användare");
        }
    }

    @Override
    public boolean deleteUserById(Long id) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            LoanServices loanServices = new LoanServices();
            User user = em.find(User.class, id);

            if (user != null) {
                // Check for active loans
                if (!loanServices.activeLoans(user, em).isEmpty()) {
                    return false;
                }
                em.getTransaction().begin();
                em.remove(user);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public boolean deleteUserByUsername(String username) {
        User user = findByUsername(username);
        if (user != null) {
            return deleteUserById(user.getUserId());
        }
        return false;
    }

    @Override
    public User findById(Long id) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.find(User.class, id);
        }
    }

    @Override
    public User findByEmail(String email) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.createQuery("""
                SELECT u FROM User u
                WHERE u.email = :email
                """,
                User.class
                )
                .setParameter("email", email)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findByEmailOrUsername(String email, String username) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email OR u.username = :username",
                    User.class)
                .setParameter("email", email)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
        }
    }

    @Override
    public List<User> findAllUsers() {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        }
    }
}
