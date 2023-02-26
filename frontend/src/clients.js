
const checkStatus = response => {
    console.log(response)
    if (response.ok) {
        return response;
    }
    const error = new Error(response.statusText);
    error.response = response;
    console.log(error)
    return Promise.reject(error);
}

export const getProfile = () => {
    const requestOptions = {
        method: 'GET',
        headers: {
            'Authorization': "Bearer " + localStorage.getItem("jwt")
        }
    };
    return fetch("/api/v1/user/profile", requestOptions)
        .then(checkStatus)
}

export const postWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + localStorage.getItem("jwt") 
        },
        body: JSON.stringify(payload)
    };
    return fetch(path, requestOptions)
    .then(checkStatus)
}

export const post = (path, payload) => {
    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
    };
    return fetch(path, requestOptions)
        .then(response => checkStatus(response))
}

export const putWithJwt = (path, payload) => {
    const requestOptions = {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': "Bearer " + localStorage.getItem("jwt") 
        },
        body: JSON.stringify(payload)
    };
    return fetch(path, requestOptions)
    .then(checkStatus)
}

// export const redirect = (pathname) => {
//     const requestOptions = {
//         crossDomain:true,
//         method: 'GET',
//         headers: {
//             'Authorization': "Bearer " + localStorage.getItem("jwt") 
//         }
//     };
//     return fetch("/api/v1/shorturl/redirect/" + pathname, requestOptions)
//         .then(response => checkStatus(response))
// }

// export const redirectJson = (pathname) => {
//     const requestOptions = {
//         crossDomain:true,
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json',
//             'Authorization': "Bearer " + localStorage.getItem("jwt") ,
//             'jwt_authentication': "Bearer " + localStorage.getItem("jwt") ,
//             'Access-Control-Allow-Origin': '*'
//         },
//         body: JSON.stringify({ shortUrlpath: pathname })
//     };
//     console.log(requestOptions);
//     return fetch("/api/v1/shorturl/redirect-json", requestOptions)
//         .then(response => checkStatus(response))
// }
