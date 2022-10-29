package pl.lodz.p.it.pas.repository.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.p.it.pas.repository.CustomRepository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@ApplicationScope
@Transactional
public class RentRepository implements CustomRepository<Rent> {

    @PersistenceContext
    EntityManager em;


    @Override
    public synchronized Rent add(Rent rent) {

        Optional<Room> room = Optional.ofNullable(em.find(Room.class, rent.getRoom().getId()));

        if (room.isEmpty()) {
            throw new RuntimeException("Room not found");
        }

        em.lock(room.get(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        boolean isColliding = isColliding(rent.getBeginTime(),
                                          rent.getEndTime(),
                                          rent.getRoom().getRoomNumber());

        if (isColliding) {
            return null;
        }

        em.persist(rent);

        return rent;
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
    public Optional<Rent> getById(Long id) {
        return Optional.ofNullable(em.find(Rent.class, id));
    }

    @Override
    public List<Rent> getAll() {
        return em.createNamedQuery("Rent.getAll", Rent.class).getResultList();
    }

    public List<Rent> getByRoomId(Long roomId) {
        return em.createNamedQuery("Rent.getByRoomId", Rent.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }

    public List<Rent> getByClientUsername(String username) {
        return em.createNamedQuery("Rent.getByClientUsername", Rent.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Rent> getByClientId(Long clientId) {
        return em.createNamedQuery("Rent.getByClientId", Rent.class)
                .setParameter("id", clientId)
                .getResultList();
    }

    public Optional<Rent> update(Rent rent) {
        return Optional.ofNullable(em.merge(rent));
    }

    private boolean isColliding(LocalDateTime beginDate, LocalDateTime endDate, int roomNumber) {
        List<Rent> rentsColliding = em.createNamedQuery("Rent.getRentsColliding", Rent.class)
                                      .setParameter("beginDate", beginDate)
                                      .setParameter("endDate", endDate)
                                      .setParameter("roomNumber", roomNumber)
                                      .getResultList();

        return !rentsColliding.isEmpty();
    }

    /**
     * Removes Rent with given ID.
     *
     * @param rentId
     * @return true if rent existed and was removed, false otherwise.
     */
    public boolean removeById(Long rentId) {
        Optional<Rent> rent = getById(rentId);

        if (rent.isEmpty()) {
            return false;
        }

        em.remove(rent.get());
        return true;
    }

    /**
     * @param roomId ID of the room.
     * @param past Flag indicating, whether past or active rents are returned.
     * @return Past rents if past is false, active (future) rents otherwise.
     */
    public List<Rent> findByRoomAndStatus(Long roomId, boolean past) {
        TypedQuery<Rent> query;
        if (past) {
            query = em.createNamedQuery("Rent.getPastRentsByRoom", Rent.class);
        } else {
            query = em.createNamedQuery("Rent.getActiveRentsByRoom", Rent.class);
        }

        return query.setParameter("id", roomId)
                    .getResultList();
    }

    public List<Rent> findByClientAndStatus(Long clientId, boolean past) {
        TypedQuery<Rent> query;
        if (past) {
            query = em.createNamedQuery("Rent.getPastRentsByClient", Rent.class);
        } else {
            query = em.createNamedQuery("Rent.getActiveRentsByClient", Rent.class);
        }

        return query.setParameter("id", clientId)
                    .getResultList();
    }
}
