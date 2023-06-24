
const webAuthn = new WebAuthn({
  callbackPath: '/q/webauthn/callback',
  registerPath: '/q/webauthn/register',
  loginPath: '/q/webauthn/login'
});

function webAuthnLogin() {
  const result = document.getElementById('result');

  const loginButton = document.getElementById('login');

  loginButton.onclick = () => {
    const userName = document.getElementById('userNameLogin').value;

    result.replaceChildren();

    webAuthn.loginOnly({ name: userName })
      .then(body => {
        const registerBody = {
          webAuthnId: body.id,
          webAuthnRawId: body.rawId,
          webAuthnResponseClientDataJSON: body.response.clientDataJSON,
          webAuthnResponseAuthenticatorData: body.response.authenticatorData,
          webAuthnResponseSignature: body.response.signature,
          webAuthnResponseUserHandle: body.response.userHandle,
          webAuthnType: body.type,
          userEmail: userName
        }
        return registerBody
      }).
      then(async (body) => {
        let formBody = []

        for (let property in body) {
          let encondedKey = encodeURIComponent(property);
          let encondedValue = encodeURIComponent(body[property]);
          formBody.push(`${encondedKey}=${encondedValue}`)
        }

        formBody = formBody.join("&");

        const response = await fetch('/api/users/webauthn/login', {
          method: 'POST',
          body: formBody,
          headers: {
            "Content-Type": "application/x-www-form-urlencoded"
          }
        });
        return response.json();
      })
      .then(body => {
        result.append(`Welcome: ${body.user.name}`)
      })
      .catch(err => {
        console.error(`Login failed: ${err}`)
      });
    return false;
  };

}

function webAuthnRegister() {
  const registerButton = document.getElementById('register');

  registerButton.onclick = () => {
    const userEmail = document.getElementById('userNameRegister').value;
    const name = document.getElementById('userName').value

    webAuthn.registerOnly({ name: userEmail, displayName: name })
      .then((body) => {
        const registerBody = {
          webAuthnId: body.id,
          webAuthnRawId: body.rawId,
          webAuthnResponseClientDataJSON: body.response.clientDataJSON,
          webAuthnType: body.type,
          webAuthnResponseAttestationObject: body.response.attestationObject,
          userEmail: userEmail,
          name: name
        }
        return registerBody
      })
      .then(async (body) => {
        let formBody = []

        for (let property in body) {
          let encondedKey = encodeURIComponent(property);
          let encondedValue = encodeURIComponent(body[property]);
          formBody.push(`${encondedKey}=${encondedValue}`)
        }

        formBody = formBody.join("&");

        return await fetch('/api/users/webauthn/register', {
          method: 'POST',
          body: formBody,
          headers: {
            // "accept":"application/json",
            "Content-Type": "application/x-www-form-urlencoded"
          }
        });
      })
      .then(body => {
        console.log(body)
      })
      .catch(err => {
        result.append("Registration failed: " + err);
      });

    return false;
  };
}

function webAuthnActivate() {
  const activateButton = document.getElementById('activate');

  activateButton.onclick = () => {
    const userEmail = document.getElementById('userNameActivate').value;



    webAuthn.registerOnly({ name: userEmail, displayName: "" })
      .then((body) => {
        const registerBody = {
          webAuthnId: body.id,
          webAuthnRawId: body.rawId,
          webAuthnResponseClientDataJSON: body.response.clientDataJSON,
          webAuthnType: body.type,
          webAuthnResponseAttestationObject: body.response.attestationObject,
          userEmail: userEmail
        }
        return registerBody
      })
      .then(async (body) => {
        let formBody = []

        for (let property in body) {
          let encondedKey = encodeURIComponent(property);
          let encondedValue = encodeURIComponent(body[property]);
          formBody.push(`${encondedKey}=${encondedValue}`)
        }

        formBody = formBody.join("&");

        const response = await fetch('/api/users/webauthn/activate', {
          method: 'POST',
          body: formBody,
          headers: {
            // "accept":"application/json",
            "Content-Type": "application/x-www-form-urlencoded"
          }

        });

        console.log(response.body)
      })
      .catch(err => {
        result.append("Registration failed: " + err);
      });

    return false;
  };
}

webAuthnLogin();
webAuthnRegister()
webAuthnActivate();
