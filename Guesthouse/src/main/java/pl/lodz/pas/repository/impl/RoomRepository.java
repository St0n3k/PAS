package pl.lodz.pas.repository.impl;

import java.util.List;
import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.pas.repository.Repository;

@ApplicationScoped
@Transactional
public class RoomRepository implements Repository<Room> {

    @PersistenceContext
    private EntityManager em;


    /**
     * Method which saved room to database, room number has to be unique, otherwise method will throw exception
     *
     * @param room room to be saved
     * @return saved room
     */
    @Override
    public Room add(Room room) {
        em.persist(room);
        return room;
    }

    @Override
    public void remove(Room room) {
        em.remove(em.merge(room));
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
        List<Room> result = em.createNamedQuery("Room.getByRoomNumber", Room.class)
                              .setParameter("roomNumber", roomNumber)
                              .getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    /**
     * @param id id to be checked among rooms
     * @return true if a room with given id exists
     */
    public boolean existsById(Long id) {
        return em.createNamedQuery("Room.existsById", Boolean.class)
                 .setParameter("id", id)
                 .getSingleResult();
    }
}
