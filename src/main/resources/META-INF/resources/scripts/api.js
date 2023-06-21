
const webAuthn = new WebAuthn({
    callbackPath: '/q/webauthn/callback',
    registerPath: '/q/webauthn/register',
    loginPath: '/q/webauthn/login'
  });

function webAuthnLogin(){
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
            webAuthnResponseSignature:body.response.signature,
            webAuthnResponseUserHandle:body.response.userHandle,
            webAuthnType:body.type,
            userName: userName
          }
          return registerBody
        }).
        then(async(body)=>{
          let formBody = []

          for (let property in body){
            let encondedKey = encodeURIComponent(property);
            let encondedValue = encodeURIComponent(body[property]);
            formBody.push(`${encondedKey}=${encondedValue}`)
          }

          formBody = formBody.join("&");

          const req = await fetch('/api/users/webauthn/login', {
              method: 'POST',
              body: formBody,
              headers: {
                  "Content-Type": "application/x-www-form-urlencoded"
              }
          });
          console.log(req.body)
          result.append("Welcome "+ req.body.name)
        })
        .catch(err => {
          result.append("Login failed: "+err);
        });
        return false;
      };
    
}

 function webAuthnRegister(){
    const registerButton = document.getElementById('register');
      
    registerButton.onclick = () => {
      const userName = document.getElementById('userNameRegister').value;
      const firstName = document.getElementById('firstName').value;
      const lastName = document.getElementById('lastName').value;
      const result = document.getElementById('result');
      result.replaceChildren();


      webAuthn.registerOnly({ name: userName, displayName: firstName + " " + lastName })
        .then((body) => {
            const registerBody = {
              webAuthnId: body.id,
              webAuthnRawId: body.rawId,
              webAuthnResponseClientDataJSON: body.response.clientDataJSON,
              webAuthnType:body.type,
              webAuthnResponseAttestationObject: body.response.attestationObject,
              userName: userName
            }
            return registerBody
          })
        .then(async (body)=>{
          let formBody = []

          for (let property in body){
            let encondedKey = encodeURIComponent(property);
            let encondedValue = encodeURIComponent(body[property]);
            formBody.push(`${encondedKey}=${encondedValue}`)
          }

          formBody = formBody.join("&");

          const req = await fetch('/api/users/webauthn/register', {
              method: 'POST',
              body: formBody,
              headers: {
                  // "accept":"application/json",
                  "Content-Type": "application/x-www-form-urlencoded"
              }
          });
          console.log(req)
          result.append("Welcome "+ req.name)
        })
        .catch(err => {
          result.append("Registration failed: "+err);
        });

      return false;
    };
}

// function webAuthnRegister(){
//   const registerButton = document.getElementById('register');

//       registerButton.onclick = () => {
//         var userName = document.getElementById('userNameRegister').value;
//         var firstName = document.getElementById('firstName').value;
//         var lastName = document.getElementById('lastName').value;
//         result.replaceChildren();
//         webAuthn.register({ name: userName, displayName: firstName + " " + lastName })
//           .then(body => {
//             result.append("User: "+userName);
//           })
//           .catch(err => {
//             result.append("Registration failed: "+err);
//           });
//         return false;
//       };
// }

// function webAuthnLogin(){
//   const loginButton = document.getElementById('login');

//       loginButton.onclick = () => {
//         var userName = document.getElementById('userNameLogin').value;
//         result.replaceChildren();
//         webAuthn.login({ name: userName })
//           .then(body => {
//             result.append("User: "+userName);
//           })
//           .catch(err => {
//             result.append("Login failed: "+err);
//           });
//         return false;
//       };
// }
webAuthnLogin();
webAuthnRegister();
