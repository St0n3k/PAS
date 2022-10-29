package pl.lodz.pas.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pl.lodz.pas.model.user.User;
import pl.lodz.pas.repository.Repository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class UserRepository implements Repository<User> {

    @PersistenceContext
    EntityManager em;

    @Override
    public User add(User user) {
        em.persist(user);
        return user;
    }

    @Override
    public void remove(User user) {
        em.remove(em.merge(user));
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public List<User> getAll() {
        return em.createNamedQuery("User.getAll", User.class).getResultList();
    }

    public Optional<User> getUserByUsername(String username) {
        List<User> result = em
                .createNamedQuery("User.getByUsername", User.class)
                .setParameter("username", username)
                .getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    public List<User> matchUserByUsername(String username) {
        return em.createNamedQuery("User.matchByUsername", User.class)
                .setParameter("username", '%' + username + '%')
                .getResultList();

    }

    public List<User> getAllUsers() {
        return em.createNamedQuery("User.getAll", User.class).getResultList();
    }

    @Override
    public Optional<User> update(User user) {
        return Optional.ofNullable(em.merge(user));
    }
}
