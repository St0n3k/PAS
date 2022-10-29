package pl.lodz.p.it.pas.repository.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;
import pl.lodz.p.it.pas.model.user.ClientTypes.ClientType;
import pl.lodz.p.it.pas.repository.CustomRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;


@Repository
@ApplicationScope
@Transactional
public class ClientTypeRepository implements CustomRepository<ClientType> {

    @PersistenceContext
    EntityManager em;

    @Override
    public ClientType add(ClientType clientType) {
        try {
            em.persist(clientType);
            return clientType;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean remove(ClientType clientType) {
        try {
            em.remove(em.merge(clientType));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<ClientType> getById(Long id) {
        return Optional.ofNullable(em.find(ClientType.class, id));
    }

    @Override
    public Optional<ClientType> update(ClientType clientType) {
        return Optional.ofNullable(em.merge(clientType));
    }

    @Override
    public List<ClientType> getAll() {
        return em.createNamedQuery("ClientType.getAll", ClientType.class).getResultList();
    }

    public Optional<ClientType> getByType(Class type) {
        return Optional.ofNullable(em.createNamedQuery("ClientType.getByType", ClientType.class)
                .setParameter("type", type.getSimpleName())
                .getSingleResult());
    }
}
