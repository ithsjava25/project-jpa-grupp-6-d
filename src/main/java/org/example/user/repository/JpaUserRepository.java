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
    }

    @Override
    public User updateUser(User user) {
        try (EntityManager em = EMFactory.getEntityManager()) {
            em.getTransaction().begin();
            User updatedUser = em.merge(user);
            em.getTransaction().commit();
            return updatedUser;
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
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
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

    @Override
    public List<User> findAllUsers() {
        try (EntityManager em = EMFactory.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        }
    }
}
