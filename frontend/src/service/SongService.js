import {SERVER_URL} from "../Constants";
import {request} from "../utilities/JWTAuth";

export function getSongs() {
    return request({
        url: SERVER_URL + "/song",
        method: 'GET'
    });
}

export function getSong(id) {

    return request({
        url: SERVER_URL + "/song/" + id,
        method: 'GET'
    });
}

// export function getAlbumsForSong(id) {
//
//     return request({
//         url: SERVER_URL + "/song/" + id + "/albums",
//         method: 'GET'
//     });
// }

export function deleteSong(songRequest) {
    const id = songRequest.id;
    return request({
        url: SERVER_URL + "/song/" + id,
        method: 'DELETE'
    });
}


export function createSong(songRequest) {
    return request({
        url: SERVER_URL + "/song",
        method: 'POST',
        body: JSON.stringify(songRequest)
    });
}

export function updateSong(songRequest) {
    const id = songRequest.id;
    return request({
        url: SERVER_URL + "/song/" + id,
        method: 'PUT',
        body: JSON.stringify(songRequest)
    });
}
