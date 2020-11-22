import {SERVER_URL} from "../Constants";
import {request} from "../utilities/JWTAuth";

export function getArtists() {
    return request({
        url: SERVER_URL + "/artist",
        method: 'GET'
    });
}

export function getArtist(id) {

    return request({
        url: SERVER_URL + "/artist/" + id,
        method: 'GET'
    });
}

export function getAlbumsForArtist(id) {

    return request({
        url: SERVER_URL + "/artist/" + id + "/albums",
        method: 'GET'
    });
}

export function getSongsForArtist(id) {

    return request({
        url: SERVER_URL + "/artist/" + id + "/songs",
        method: 'GET'
    });
}

export function deleteArtist(artistRequest) {
    const id = artistRequest.id;
    return request({
        url: SERVER_URL + "/artist/" + id,
        method: 'DELETE'
    });
}


export function createArtist(artistRequest) {
    return request({
        url: SERVER_URL + "/artist",
        method: 'POST',
        body: JSON.stringify(artistRequest)
    });
}

export function updateArtist(artistRequest) {
    const id = artistRequest.id;
    return request({
        url: SERVER_URL + "/artist/" + id,
        method: 'PUT',
        body: JSON.stringify(artistRequest)
    });
}
