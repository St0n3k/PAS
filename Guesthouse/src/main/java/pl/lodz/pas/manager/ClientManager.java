package pl.lodz.pas.manager;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.pas.model.Address;
import pl.lodz.pas.model.Client;
import pl.lodz.pas.model.ClientTypes.ClientType;
import pl.lodz.pas.model.ClientTypes.Default;
import pl.lodz.pas.repository.impl.ClientRepository;
import pl.lodz.pas.repository.impl.ClientTypeRepository;

@AllArgsConstructor
@NoArgsConstructor
@RequestScoped
public class ClientManager {

    @Inject
    private ClientRepository clientRepository;
    @Inject
    private ClientTypeRepository clientTypeRepository;


    public Client registerClient(String firstName, String lastName, String personalId, String city, String street, int number) {

        //Values are validated in constructors
        Address address = new Address(city, street, number);
        ClientType defaultClientType = clientTypeRepository.getByType(Default.class);
        Client client = new Client(firstName, lastName, personalId, address, defaultClientType);

        return clientRepository.add(client);
    }

    public Client getByPersonalId(String personalId) {
        return clientRepository.getClientByPersonalId(personalId);
    }

    public boolean removeClient(Client client) {
        return clientRepository.remove(client);
    }

    public Client updateClient(Client client) {
        return clientRepository.update(client);
    }

    public Client changeTypeTo(Class type, Client client) {
        client.setClientType(clientTypeRepository.getByType(type));

        client = updateClient(client);
        return client;
    }
}
