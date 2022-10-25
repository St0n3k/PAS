package pl.lodz.pas.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.Repository;

import java.util.List;

//TODO removing a room should check if there are no rents for this room

@ApplicationScoped
@Transactional
public class RoomRepository implements Repository<Room> {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Room add(Room room) {
        em.persist(room);
        return room;
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
    public Room getById(Long id) {
        try {
            return em.find(Room.class, id);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @Override
    public Room update(Room room) {
        try {
            return em.merge(room);
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    @Override
    public List<Room> getAll() {
        return em.createNamedQuery("Room.getAll", Room.class).getResultList();
    }

    public Room getByRoomNumber(int roomNumber) {
        List<Room> result = em
                .createNamedQuery("Room.getByRoomNumber", Room.class)
                .setParameter("roomNumber", roomNumber)
                .getResultList();

        if (result.isEmpty()) {
            throw new NotFoundException("Room not found");
        } else {
            return result.get(0);
        }
    }
}
