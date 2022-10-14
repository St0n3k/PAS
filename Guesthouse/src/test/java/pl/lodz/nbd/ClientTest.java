package pl.lodz.nbd;

import org.junit.jupiter.api.Test;
import pl.lodz.nbd.manager.ClientManager;
import pl.lodz.nbd.model.Client;
import pl.lodz.nbd.repository.impl.ClientRepository;
import pl.lodz.nbd.repository.impl.ClientTypeRepository;

import static org.junit.jupiter.api.Assertions.*;


public class ClientTest {

    private static final ClientRepository clientRepository = new ClientRepository();
    private static final ClientTypeRepository clientTypeRepository = new ClientTypeRepository();


    @Test
    void registerAndUpdateClientTest() {
        ClientManager clientManager = new ClientManager(clientRepository, clientTypeRepository);

        //Check if clients are persisted
        assertNotNull(clientManager.registerClient("Jakub", "Konieczny", "000333", "Warszawa", "Gorna", 16));
        assertNotNull(clientManager.registerClient("Anna", "Matejko", "000222", "Łódź", "Wesoła", 32));

        //Check if client is not persisted (existing personalId)
        assertNull(clientManager.registerClient("Mateusz", "Polak", "000222", "Kraków", "Słoneczna", 133));

        //Check if getByPersonalId works properly
        assertNotNull(clientManager.getByPersonalId("000333"));
        assertNotNull(clientManager.getByPersonalId("000222"));
        assertNull(clientManager.getByPersonalId("000444"));
    }

    @Test
    void updateClientTest() {
        ClientManager clientManager = new ClientManager(clientRepository, clientTypeRepository);

        clientManager.registerClient("Jan", "Matejko", "000211", "Łódź", "Wesoła", 32);
        Client client = clientManager.getByPersonalId("000211");
        assertEquals(client.getVersion(), 0);

        client.setFirstName("Marcin");
        Client client2 = clientManager.updateClient(client);

        //Check if version field incremented
        assertEquals(client2.getVersion(), 1);
        assertEquals(client2.getFirstName(), "Marcin");
    }


}
