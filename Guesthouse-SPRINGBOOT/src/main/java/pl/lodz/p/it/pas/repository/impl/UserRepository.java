package pl.lodz.p.it.pas.repository.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.p.it.pas.repository.CustomRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;


@Repository
@ApplicationScope
@Transactional
public class UserRepository implements CustomRepository<User> {

    @PersistenceContext
    EntityManager em;

    @Override
    public User add(User user) {
        em.persist(user);
        return user;
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
