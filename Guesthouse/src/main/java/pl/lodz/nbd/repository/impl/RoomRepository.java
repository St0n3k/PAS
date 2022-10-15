package pl.lodz.nbd.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import pl.lodz.nbd.common.EntityManagerCreator;
import pl.lodz.nbd.model.Room;
import pl.lodz.nbd.repository.Repository;

import java.util.List;

@ApplicationScoped
public class RoomRepository implements Repository<Room> {
    @Override
    public Room add(Room room) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(room);
            em.getTransaction().commit();
            return room;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(Room room) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            em.getTransaction().begin();
            em.remove(em.merge(room));
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Room getById(Long id) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            return em.find(Room.class, id);
        }
    }

    @Override
    public Room update(Room room) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            em.getTransaction().begin();
            Room newRoom = em.merge(room);
            em.getTransaction().commit();
            return newRoom;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Room> getAll() {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
            return em.createNamedQuery("Room.getAll", Room.class).getResultList();
        }
    }

    public Room getByRoomNumber(int roomNumber) {
        try (EntityManager em = EntityManagerCreator.getEntityManager()) {
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
