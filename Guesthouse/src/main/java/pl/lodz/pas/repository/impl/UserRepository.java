package pl.lodz.pas.repository.impl;

import java.util.List;
import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pl.lodz.p.it.pas.model.user.User;
import pl.lodz.pas.repository.Repository;

@ApplicationScoped
@Transactional
public class UserRepository implements Repository<User> {

    @PersistenceContext
    EntityManager em;

    /**
     * Method which saves user to database, username of user has to be unique, otherwise exception will be thrown
     *
     * @param user user to be saved
     * @return saved user
     */
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

    /**
     * @param phrase phrase to be matched among usernames
     * @return list of users, whose usernames contain given phrase
     */
    public List<User> matchUserByUsername(String phrase) {
        return em.createNamedQuery("User.matchByUsername", User.class)
                 .setParameter("username", '%' + phrase + '%')
                 .getResultList();

    }

    public List<User> getUsersByRole(String role) {
        return em.createNamedQuery("User.getByRole", User.class)
                 .setParameter("role", '%' + role + '%')
                 .getResultList();

    }

    public List<User> getAllUsers() {
        return em.createNamedQuery("User.getAll", User.class).getResultList();
    }

    @Override
    public Optional<User> update(User user) {
        return Optional.ofNullable(em.merge(user));
    }

    public List<User> getUsersByRoleAndMatchingUsername(String role, String username) {
        return em.createNamedQuery("User.getByRoleMatchingName", User.class)
                 .setParameter("role", "%" + role + "%")
                 .setParameter("username", "%" + username + "%")
                 .getResultList();
    }
}
