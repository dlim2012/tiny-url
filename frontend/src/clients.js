


const checkStatus = response => {
    console.log(response)
    if (response.ok) {
        return response;
    }
    const error = new Error(response.statusText);
    error.response = response;
    return Promise.reject(error);
}

export const getProfile = (jwt_authentication) => {
    const requestOptions = {
        method: 'GET',
        headers: {
            'Authorization': "Bearer " + jwt_authentication
        }
    };
    return fetch("/api/v1/user/profile", requestOptions)
        .then(checkStatus)
}

export const getUrl = (jwt_authentication) => {
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + jwt_authentication
        },
        body: JSON.stringify({ isActive: 2})
    };
    return fetch("/api/v1/user/urls", requestOptions)
    .then(checkStatus)
}

export const getGenerateUrlResponse = (jwt_authentication, payload) => {
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + jwt_authentication
        },
        body: JSON.stringify(payload)
    };
    return fetch("/api/v1/user/generate", requestOptions)
        .then(checkStatus)
}

export const getShortUrls = (jwt_authentication, longUrl) => {
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + jwt_authentication
        },
        body: JSON.stringify({ longUrl: longUrl })
    };
    return fetch("/api/v1/shorturl/short", requestOptions)
        .then(response => checkStatus(response))
}

export const getExtensionUrlResponse = (jwt_authentication, payload) => {
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + jwt_authentication
        },
        body: JSON.stringify(payload)
    };
    return fetch("/api/v1/user/extend", requestOptions)
        .then(response => checkStatus(response))
}
