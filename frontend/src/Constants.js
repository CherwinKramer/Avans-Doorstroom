export const ACCESS_TOKEN = 'accessToken';
export const SERVER_URL = "http://localhost:8080/api";

export const MENU = [
    { label: 'Dashboard', icon: 'pi pi-fw pi-home', to: '/' },
    { label: 'Artist', icon: 'pi pi-fw pi-user', to: '/artist' },
    { label: 'Album', icon: 'pi pi-fw pi-briefcase', to: '/album' },
    { label: 'Song', icon: 'pi pi-fw pi-align-justify', to: '/song' },
    { label: 'Genre', icon: 'pi pi-fw pi-palette', to: '/genre' },
    { label: 'View Source', icon: 'pi pi-fw pi-search', command: () => { window.location = "https://github.com/primefaces/sigma-react" } }
];
