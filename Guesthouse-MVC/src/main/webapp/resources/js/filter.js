function filterClients() {
    let input = document.getElementById("filterInput");
    let apiUrl = "/api/users/clients?username=" + input.value;
    fetch(apiUrl)
        .then((response) => response.json())
        .then((data) => {
            let table = document.getElementById("filterTable");
            let tbody = table.getElementsByTagName("tbody")[0];
            tbody.innerHTML = "";

            for (let client of data) {
                let tr = document.createElement("tr");

                let user_id = document.createElement("td");
                user_id.innerText = client.id;
                let username = document.createElement("td");
                username.innerText = client.username;
                let firstName = document.createElement("td");
                firstName.innerText = client.firstName;
                let lastName = document.createElement("td");
                lastName.innerText = client.lastName;
                let personalId = document.createElement("td");
                personalId.innerText = client.personalId;
                let clientType = document.createElement("td");
                clientType.innerText = client.clientType.name;
                let city = document.createElement("td");
                city.innerText = client.address.city;
                let street = document.createElement("td");
                street.innerText = client.address.street;
                let houseNumber = document.createElement("td");
                houseNumber.innerText = client.address.houseNumber;
                let active = document.createElement("td");

                let checkbox = document.createElement("input")
                checkbox.type = "checkbox";
                checkbox.checked = client.active;
                checkbox.setAttribute("style", "pointer-events: none;");
                active.appendChild(checkbox);

                tr.appendChild(user_id);
                tr.appendChild(username);
                tr.appendChild(active);
                tr.appendChild(firstName);
                tr.appendChild(lastName);
                tr.appendChild(personalId);
                tr.appendChild(clientType);
                tr.appendChild(city);
                tr.appendChild(street);
                tr.appendChild(houseNumber);

                tbody.appendChild(tr);
            }
        });
}