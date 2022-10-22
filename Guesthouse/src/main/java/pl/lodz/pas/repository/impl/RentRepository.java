package pl.lodz.pas.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.RollbackException;
import jakarta.transaction.Transactional;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Transactional
public class RentRepository implements Repository<Rent> {

    @PersistenceContext
    EntityManager em;

    @Transactional
    @Override
    public Rent add(Rent rent) {
        try {
            Room room = em.find(Room.class, rent.getRoom().getId());

            em.lock(room, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            boolean isColliding = isColliding(
                    rent.getBeginTime(),
                    rent.getEndTime(),
                    room.getRoomNumber());

            if (isColliding) return null;

            em.persist(rent);

            return rent;
        } catch (RollbackException e) {
            System.out.println("Repeating transaction");
            //We have to set id to null, because in transaction,
            //rent gets id on persist, but on failed transaction it not gets back to null
            rent.setId(null);
            return add(rent);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(Rent rent) {
        try {
            em.remove(em.merge(rent));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Rent getById(Long id) {
        try {
            return em.find(Rent.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Rent> getAll() {
        try {
            return em.createNamedQuery("Rent.getAll", Rent.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Rent> getByRoomNumber(int roomNumber) {
        try {
            return em.createNamedQuery("Rent.getByRoomNumber", Rent.class).setParameter("roomNumber", roomNumber).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Rent> getByClientPersonalId(String personalId) {
        try {
            return em.createNamedQuery("Rent.getByClientPersonalId", Rent.class).setParameter("personalId", personalId).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Rent update(Rent rent) {
        try {
            return em.merge(rent);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isColliding(LocalDateTime beginDate, LocalDateTime endDate, int roomNumber) {
        try {
            List<Rent> rentsColliding =
                    em.createNamedQuery("Rent.getRentsColliding", Rent.class)
                            .setParameter("beginDate", beginDate)
                            .setParameter("endDate", endDate)
                            .setParameter("roomNumber", roomNumber)
                            .getResultList();

            return !rentsColliding.isEmpty();
        } catch (Exception e) {
            System.out.println("Unexpected exc");
            return true;
        }
    }

}