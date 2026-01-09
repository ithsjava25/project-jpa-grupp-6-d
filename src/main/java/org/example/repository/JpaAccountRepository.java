package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.EMFactory;
import org.example.User;

import java.util.List;

public class JpaAccountRepository implements AccountRepository{

    @Override
    public User createAccount(User user) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        }
    }

    @Override
    public User updateAccount(User user) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            em.getTransaction().begin();
            User updatedUser = em.merge(user);
            em.getTransaction().commit();
            return updatedUser;
        }
    }

    @Override
    public boolean deleteAccountById(Long id) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public boolean deleteAccountByUsername(String username) {
        User user = findByUsername(username);
        if (user != null) {
            return deleteAccountById(user.getUserId());
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
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> findAllUsers() {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        }
    }
}
