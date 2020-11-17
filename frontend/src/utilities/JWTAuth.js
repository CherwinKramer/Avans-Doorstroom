import {ACCESS_TOKEN, SERVER_URL} from "../Constants";

export const request = (options, headers = null) => {

    if (headers === null) {
        headers = {
            'Content-Type': 'application/json',
        };
    }

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.Authorization = 'Bearer ' + localStorage.getItem(ACCESS_TOKEN);
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
        .then(response =>
            response.json().then(json => {
                if (!response.ok) {
                    return Promise.reject(json);
                }
                return json;
            })
        );
};

export function handleLogin(loginRequest) {
    return request({
        url: SERVER_URL + "/auth/login",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function handleRegistration(registerRequest) {
    return request({
        url: SERVER_URL + "/auth/register",
        method: 'POST',
        body: JSON.stringify(registerRequest)
    });
}

export function getCurrentUser() {
    if (!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: SERVER_URL + "/auth/me",
        method: 'GET'
    });
}
