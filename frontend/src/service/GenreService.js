import {SERVER_URL} from "../Constants";
import {request} from "../utilities/JWTAuth";

export function getGenres() {
    return request({
        url: SERVER_URL + "/genre",
        method: 'GET'
    });
}

export function getGenre(id) {

    return request({
        url: SERVER_URL + "/genre/" + id,
        method: 'GET'
    });
}

export function deleteGenre(genreRequest) {
    const id = genreRequest.id;
    return request({
        url: SERVER_URL + "/genre/" + id,
        method: 'DELETE'
    });
}


export function createGenre(genreRequest) {
    return request({
        url: SERVER_URL + "/genre",
        method: 'POST',
        body: JSON.stringify(genreRequest)
    });
}

export function updateGenre(genreRequest) {
    const id = genreRequest.id;
    return request({
        url: SERVER_URL + "/genre/" + id,
        method: 'PUT',
        body: JSON.stringify(genreRequest)
    });
}
