package pl.lodz.p.it.pas.repository.impl;
//
//import java.util.List;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
//import pl.lodz.pas.model.user.User;
//import pl.lodz.pas.repository.Repository;
//
//@ApplicationScoped
//@Transactional
public class UserRepository {
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Override
//    public User add(User user) {
//        try {
//            em.persist(user);
//            return user;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @Override
//    public boolean remove(User user) {
//        try {
//            em.remove(em.merge(user));
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public User getById(Long id) {
//        try {
//            return em.find(User.class, id);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @Override
//    public List<User> getAll() {
//        try {
//            return em.createNamedQuery("User.getAll", User.class).getResultList();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public User getUserByUsername(String username) {
//        try {
//            List<User> result = em.createNamedQuery("User.getByUsername", User.class)
//                                  .setParameter("username", username)
//                                  .getResultList();
//            if (result.isEmpty()) {
//                return null;
//            } else {
//                return result.get(0);
//            }
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public List<User> matchUserByUsername(String username) {
//        try {
//            List<User> result = em.createNamedQuery("User.matchByUsername", User.class)
//                                  .setParameter("username", '%' + username + '%')
//                                  .getResultList();
//            if (result.isEmpty()) {
//                return null;
//            } else {
//                return result;
//            }
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public List<User> getAllUsers() {
//        try {
//            return em.createNamedQuery("User.getAll", User.class).getResultList();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @Override
//    public User update(User user) {
//        try {
//            return em.merge(user);
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
