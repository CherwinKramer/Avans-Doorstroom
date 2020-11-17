import {SERVER_URL} from "../Constants";
import {request} from "../utilities/JWTAuth";

export function getAlbums() {
    return request({
        url: SERVER_URL + "/album",
        method: 'GET'
    });
}

export function getAlbum(id) {

    return request({
        url: SERVER_URL + "/album/" + id,
        method: 'GET'
    });
}

export function deleteAlbum(albumRequest) {
    const id = albumRequest.id;
    return request({
        url: SERVER_URL + "/album/" + id,
        method: 'DELETE'
    });
}


export function getSongsForAlbum(id) {
    return request({
        url: SERVER_URL + "/album/" + id + "/songs",
        method: 'GET'
    });
}

export function createAlbum(albumRequest) {
    return request({
        url: SERVER_URL + "/album",
        method: 'POST',
        body: JSON.stringify(albumRequest)
    });
}

export function updateAlbum(albumRequest) {
    const id = albumRequest.id;
    return request({
        url: SERVER_URL + "/album/" + id,
        method: 'PUT',
        body: JSON.stringify(albumRequest)
    });
}
