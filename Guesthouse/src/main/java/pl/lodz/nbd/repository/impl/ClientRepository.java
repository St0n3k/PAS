package pl.lodz.nbd.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import pl.lodz.nbd.model.Client;
import pl.lodz.nbd.repository.Repository;

import java.util.List;

@ApplicationScoped
@Transactional
public class ClientRepository implements Repository<Client> {

    @PersistenceContext
    EntityManager em;

    @Override
    public Client add(Client client) {
        try {
            em.persist(client);
            return client;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(Client client) {
        try {
            em.remove(em.merge(client));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Client getById(Long id) {
        try {
            return em.find(Client.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Client> getAll() {
        try {
            return em.createNamedQuery("Client.getAll", Client.class).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Client getClientByPersonalId(String personalId) {
        try {
            List<Client> result = em.createNamedQuery("Client.getByPersonalId", Client.class).setParameter("personalId", personalId).getResultList();

            if (result.isEmpty()) {
                return null;
            } else {
                return result.get(0);
            }
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public Client update(Client client) {
        try {
            return em.merge(client);
        } catch (Exception e) {
            return null;
        }
    }
}
