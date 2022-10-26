package pl.lodz.pas.repository.impl;

import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.Repository;

@ApplicationScoped
@Transactional
public class RoomRepository implements Repository<Room> {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Room add(Room room) {
        try {
            em.persist(room);
        } catch (Exception e) {
            return null;
        }
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
        try {
            return em.createNamedQuery("Room.getAll", Room.class).getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Room getByRoomNumber(int roomNumber) {
        try {
            List<Room> result = em
                    .createNamedQuery("Room.getByRoomNumber", Room.class)
                    .setParameter("roomNumber", roomNumber)
                    .getResultList();

            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            return null;
        }

    }
}
