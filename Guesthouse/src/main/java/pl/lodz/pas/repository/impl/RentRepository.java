package pl.lodz.pas.repository.impl;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.hibernate.StaleObjectStateException;
import pl.lodz.pas.model.Rent;
import pl.lodz.pas.model.Room;
import pl.lodz.pas.repository.Repository;

@ApplicationScoped
@Transactional
public class RentRepository implements Repository<Rent> {

    @PersistenceContext
    EntityManager em;


    @Override
    public Rent add(Rent rent) {
        try {
            Room room = em.find(Room.class, rent.getRoom().getId());

            em.lock(room, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            boolean isColliding = isColliding(
                rent.getBeginTime(),
                rent.getEndTime(),
                rent.getRoom().getRoomNumber());

            if (isColliding) {
                return null;
            }

            em.persist(rent);

            return rent;
        } catch (StaleObjectStateException e) {
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

    public List<Rent> getByRoomId(Long roomId) {
        try {
            return em.createNamedQuery("Rent.getByRoomId", Rent.class).setParameter("roomId", roomId).getResultList();
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    public List<Rent> getByClientUsername(String username) {
        try {
            return em.createNamedQuery("Rent.getByClientUsername", Rent.class)
                     .setParameter("username", username)
                     .getResultList();
        } catch (Exception e) {
            throw new NotFoundException();
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

    /**
     * Removes Rent with given ID.
     *
     * @param rentId
     * @return true if rent existed and was removed, false otherwise.
     */
    public boolean removeById(Long rentId) {
        try {
            int deletedCount = em.createNamedQuery("Rent.removeById")
                                 .setParameter("id", rentId)
                                 .executeUpdate();
            return deletedCount == 1;
        } catch (Exception e) {
            return false;
        }
        //        try {
        //            Rent r = em.find(Rent.class, rentId);
        //            System.out.println(r);
        //            if (r == null) {
        //                return false;
        //            }
        //            em.remove(r);
        //            return true;
        //        } catch (Exception e) {
        //            return false;
        //        }
    }
}
