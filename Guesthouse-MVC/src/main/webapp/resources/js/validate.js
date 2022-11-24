function validate() {
    let ret = true;

    for (let x of document.forms.addClientForm) {
        if (x.value === "") {
            ret = false;
            x.style.borderColor = "red";
            x.title = "Required field";
        } else {
            x.style.borderColor = "";
            x.title = "";
        }
    }

    let number = document.forms.addClientForm["addClientForm:number"];
    let houseNumber = parseInt(number.value);
    if (isNaN(houseNumber) || houseNumber < 0) {
        number.style.borderColor = "red";
        number.title = "Value can't be smaller than 0";
        ret = false;
    } else {
        if (!isNaN(houseNumber)) {
            number.style.borderColor = "";
            number.title = "";
        }
    }

    let regex = new RegExp(/[^\d\s!?_#$%^&amp;*()@=+,.|/~`&apos;&quot;\\]+/);
    let personalIdRegex = new RegExp(/[0-9]+/);

    let firstName = document.forms.addClientForm["addClientForm:firstName"];
    if (!testTextInput(regex, firstName)) {
        ret = false;
    }

    let lastName = document.forms.addClientForm["addClientForm:lastName"];
    if (!testTextInput(regex, lastName)) {
        ret = false;
    }

    let personalId = document.forms.addClientForm["addClientForm:personalID"];
    if (!testTextInput(personalIdRegex, personalId)) {
        ret = false;
    }

    let city = document.forms.addClientForm["addClientForm:city"];
    if (!testTextInput(regex, city)) {
        ret = false;
    }

    let street = document.forms.addClientForm["addClientForm:street"];
    if (!testTextInput(regex, street)) {
        ret = false;
    }

    return ret;
}

function testTextInput(regex, input) {
    if (!regex.test(input.value)) {
        input.style.borderColor = "red";
        input.title = "Pattern not matched";
        return false;
    } else {
        if (input.value !== "") {
            input.style.borderColor = "";
            input.title = "";
        }
    }
    return true;
}