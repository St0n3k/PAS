package pl.lodz.p.it.pas.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.repository.CustomRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;



@Repository
@Transactional
public class RoomRepository implements CustomRepository<Room> {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Room add(Room room) {
        try {
            em.persist(room);
            return room;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(Room room) {
        try {
            em.remove(em.merge(room));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<Room> getById(Long id) {
        return Optional.ofNullable(em.find(Room.class, id));
    }

    @Override
    public Optional<Room> update(Room room) {
        return Optional.ofNullable(em.merge(room));
    }

    @Override
    public List<Room> getAll() {
        return em.createNamedQuery("Room.getAll", Room.class).getResultList();
    }

    public Optional<Room> getByRoomNumber(int roomNumber) {
        Optional<Room> room = Optional.ofNullable(em.createNamedQuery("Room.getByRoomNumber", Room.class)
                .setParameter("roomNumber", roomNumber)
                .getSingleResult());

        return room;
    }

    public boolean existsById(Long id) {
        return em.createNamedQuery("Room.existsById", Boolean.class)
                 .setParameter("id", id)
                 .getSingleResult();
    }
}
