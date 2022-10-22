package pl.lodz.pas.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pl.lodz.pas.model.user.Client;
import pl.lodz.pas.model.user.User;
import pl.lodz.pas.repository.Repository;

import java.util.List;

@ApplicationScoped
@Transactional
public class UserRepository implements Repository<User> {

    @PersistenceContext
    EntityManager em;

    @Override
    public User add(User user) {
        try {
            em.persist(user);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(User user) {
        try {
            em.remove(em.merge(user));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Client getById(Long id) {
        try {
            return em.find(Client.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> getAll() {
        try {
            return em.createNamedQuery("User.getAll", User.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Client getClientByPersonalId(String personalId) {
        try {
            List<Client> result = em.createNamedQuery("Client.getByPersonalId", Client.class).setParameter("personalId", personalId).getResultList();

            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            return null;
        }

    }

    public User getUserByUsername(String username) {
        try {
            List<User> result = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", username).getResultList();
            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> getAllUsers() {
        try {
            return em.createNamedQuery("User.getAll", User.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User update(User user) {
        try {
            return em.merge(user);
        } catch (Exception e) {
            return null;
        }
    }
}