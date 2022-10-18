package pl.lodz.nbd.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerCreator {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("test_guesthouse");

    
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
