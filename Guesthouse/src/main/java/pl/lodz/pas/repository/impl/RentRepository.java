package pl.lodz.pas.repository.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import pl.lodz.p.it.pas.model.Rent;
import pl.lodz.p.it.pas.model.Room;
import pl.lodz.pas.repository.Repository;

@ApplicationScoped
@Transactional
public class RentRepository implements Repository<Rent> {

    @PersistenceContext
    EntityManager em;

    /**
     * Synchronized method which saves Rent object to database
     * if the dates do not collide with other rents for the same room
     *
     * @param rent
     * @return
     */
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
    public void remove(Rent rent) {
        em.remove(em.merge(rent));
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

    /**
     * Method to check if given period of time is colliding with periods of existing rents for given room
     *
     * @param beginDate begin date of currently created rent
     * @param endDate end date of currently created rent
     * @param roomNumber number of the room
     * @return false if new rent can be created for given period of time
     */
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

    /**
     * @param clientId ID of the client.
     * @param past Flag indicating, whether past or active rents are returned.
     * @return Past rents if past is false, active (future) rents otherwise.
     */
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
