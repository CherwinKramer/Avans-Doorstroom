import React, {Component} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {Toolbar} from 'primereact/toolbar';
import {createSong, deleteSong, getSong, getSongs, updateSong} from "../../service/SongService";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {getAlbumsForArtist, getArtists} from "../../service/ArtistService";
import {Dropdown} from "primereact/dropdown";
import {Toast} from "primereact/toast";
import {MultiSelect} from "primereact/multiselect";
import {getGenres} from "../../service/GenreService";

export class Song extends Component {

    constructor(props) {
        super(props);

        this.state = {
            song: {
                id: '',
                name: '',
                url: '',
                artist: {
                    id: ''
                },
                album: {
                    id: ''
                },
                genre: {
                    id: ''
                },
                featuredArtists:[],
                featuredArtistNames: ''
            },
            featuredArtists:[],
            songs: [],
            genres: [],
            artists: [],
            dialogVisible: false,
            deleteDialogVisible: false
        };

        this.openEditDialog = this.openEditDialog.bind(this);
        this.resetValues = this.resetValues.bind(this);
        this.saveSong = this.saveSong.bind(this);
        this.deleteSong = this.deleteSong.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleArtistChange = this.handleArtistChange.bind(this);
        this.handleGenreChange = this.handleGenreChange.bind(this);
        this.handleFeaturedArtistChange = this.handleFeaturedArtistChange.bind(this);
        this.handleAlbumChange = this.handleAlbumChange.bind(this);
        this.findAlbumsByArtist = this.findAlbumsByArtist.bind(this);
    }

    componentDidMount() {
        getSongs().then(songData => {
            this.setState({
                songs: songData.object
            })
            getArtists().then(artistData => {
                let artistSelectListData = artistData.object.map(artist => { return {label: artist.name + ' ' + artist.surname, value: artist.id} });
                this.setState({artists: artistSelectListData});
            })

            getGenres().then(genreData => {
                let genreSelectListData = genreData.object.map(genre => { return {label: genre.name, value: genre.id} });
                this.setState({genres: genreSelectListData});
            })
        });
    }

    resetValues() {
        this.setState({
            song: {
                id: '',
                name: '',
                url: '',
                artist: {
                    id: ''
                },
                album: {
                    id: ''
                },
                genre: {
                    id: ''
                },
                featuredArtists:[],
                featuredArtistNames: ''
            },
            dialogVisible: false,
            deleteDialogVisible: false
        });
    }

    handleNameChange(event) {
        this.state.song['name'] = event.target.value;
    }

    handleArtistChange(event) {
        let song = this.state.song;
        song['artist'].id = event.value;
        this.populateCorrectFeaturedArtistSelectList(song['artist']);
        song['album'].id = '';
        song['featuredArtists'] = [];
        this.setState({song: song}, () => {
            this.findAlbumsByArtist(this.state.song.artist)
        });
    }

    handleGenreChange(event) {
        let song = this.state.song;
        song['genre'].id = event.value;
        this.setState({song: song});
    }

    handleFeaturedArtistChange(e) {
        let song = this.state.song;
        song['featuredArtists'] = e.value;
        this.setState({song: song});
    }

    handleAlbumChange(event) {
        let song = this.state.song;
        song['album'].id = event.value;
        this.setState({song: song});
    }

    saveSong() {
        let songs = this.state.songs;
        let song = this.state.song;
        let creation = !song.id;

        let failed = false;
        if (!song.name) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Name must be filled in', life: 3000});
            failed = true;
        }
        if (!song.genre.id) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Genre must be selected', life: 3000});
            failed = true;
        }
        if (!song.artist.id) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Artist must be selected', life: 3000});
            failed = true;
        }
        if (failed) return;

        if (creation) {
            createSong(song).then(r => {
                songs.push(r.object);
                this.setState({songs: songs});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        } else {
            updateSong(song).then(r => {
                songs[this.findSelectedSongIndex()] = r.object;
                this.setState({songs: songs});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        }
    }

    deleteSong() {
        let song = this.state.song;
        let songs = this.state.songs;
        let indexId = songs.map(x => x.id).indexOf(song.id);

        deleteSong(song).then(r => {
            songs.splice(indexId, 1);
            this.setState({songs: songs});
            this.resetValues();
            this.toast.show({severity:'success', detail: 'Song has been successfully deleted', life: 3000});
        }).catch(e => {
            this.toast.show({severity:'error', detail: 'Could not delete the song', life: 3000});
        });
    }

    populateCorrectFeaturedArtistSelectList(artist) {
        let featuredArtists = JSON.parse(JSON.stringify(this.state.artists));
        this.setState({
            featuredArtists: featuredArtists
        }, () => {
            featuredArtists = this.state.featuredArtists;
            let index = this.state.featuredArtists.map(x => x.value).indexOf(artist.id);
            if (index !== -1) featuredArtists.splice(index, 1);
            this.setState({
                featuredArtists: featuredArtists
            })
        })
    }

    findSelectedSongIndex() {
        let index = this.state.songs.map(x => x.id).indexOf(this.state.song.id);
        return index;
    }

    findAlbumsByArtist(artist) {
        getAlbumsForArtist(artist.id).then(albumData => {
            let albumSelectListData = albumData.object.map(album => { return {label: album.name, value: album.id} });
            this.setState({albums: albumSelectListData});
        })
    }

    openEditDialog(rowData) {
        getSong(rowData.id).then(songData => {
            this.populateCorrectFeaturedArtistSelectList(songData.object.artist);
            this.setState({
                song: songData.object,
                dialogVisible: true
            }, () => {
                this.findAlbumsByArtist(songData.object.artist);
            })
        });
    }

    render() {
        const leftToolbarTemplate = () => {
            return (
                <React.Fragment>
                    <Button label="New" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={() => {this.setState({dialogVisible: true})}} />
                </React.Fragment>
            )
        }

        const actionBodyTemplate = (rowData) => {
            return (
                <div className="actions">
                    <Button icon="pi pi-pencil" className="p-button-rounded p-button-success p-mr-2" onClick={() => {this.openEditDialog(rowData)}}/>
                    <Button icon="pi pi-trash" className="p-button-rounded p-button-danger p-mr-2" onClick={() => {this.setState({deleteDialogVisible: true, song: rowData})}}/>
                    {rowData.url && <Button icon="pi pi-play" className="p-button-rounded p-button-help" onClick={() => { window.open(rowData.url, "_blank") } } />}
                </div>
            );
        }

        const songDialogFooter = (
            <>
                <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={() => {this.setState({dialogVisible: false})}}/>
                <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={this.saveSong}/>
            </>
        );

        const deleteSongDialogFooter = (
            <>
                <Button label="No" icon="pi pi-times" className="p-button-text" onClick={() => {this.resetValues()}}/>
                <Button label="Yes" icon="pi pi-check" className="p-button-text" onClick={() => {this.deleteSong()}}/>
            </>
        );

        const multiSelectSelectedItemTemplate = (option) => {
            if (option) {
                let artists = this.state.featuredArtists;
                let indexId = artists.map(x => x.value).indexOf(option);
                option = (artists[indexId].label);
                return (
                    <div>
                        <div>{option}</div>
                    </div>
                );
            }
            return "Select a featured artist"
        }

        return (
            <div className="p-grid crud-demo">
                <Toast ref={(el) => this.toast = el} />

                <div className="p-col-12">
                    <div className="card">
                        <Toolbar className="p-mb-4" left={leftToolbarTemplate} />

                        <DataTable value={this.state.songs}
                                   dataKey="id" paginator rows={10} rowsPerPageOptions={[5, 10, 25]} className="datatable-responsive"
                                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                   currentPageReportTemplate="Showing {first} to {last} of {totalRecords} products"
                                   emptyMessage="No songs found." header="Manage songs">

                            <Column field="name" header="Name" sortable />
                            <Column field="genre.name" header="Genre" sortable />
                            <Column field="artist.fullName" header="Artist" sortable />
                            <Column field="album.name" header="Album" sortable />
                            <Column field="featuredArtistNames" header="Featured Artists" />
                            <Column body={actionBodyTemplate} />

                        </DataTable>

                        <Dialog visible={this.state.dialogVisible} style={{width: '450px'}} header="Song" modal className="p-fluid" footer={songDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="p-field">
                                <label htmlFor="name">Name</label>
                                <InputText id="name" defaultValue={this.state.song.name} onChange={(e) => this.state.song['name'] = e.target.value} required autoFocus />
                            </div>

                            <div className="p-field">
                                <label htmlFor="url">Url</label>
                                <InputText id="url" defaultValue={this.state.song.url} onChange={(e) => this.state.song['url'] = e.target.value} required autoFocus />
                            </div>

                            <div className="p-field">
                                <label htmlFor="artist">Artist</label>
                                <Dropdown value={this.state.song.artist.id} options={this.state.artists}
                                          appendTo={document.body} onChange={this.handleArtistChange}
                                          placeholder="Select an artist" />
                            </div>

                            <div className="p-field">
                                <label htmlFor="genre">Genre</label>
                                <Dropdown value={this.state.song.genre.id} options={this.state.genres}
                                          appendTo={document.body} onChange={this.handleGenreChange}
                                          placeholder="Select a genre" />
                            </div>

                            <div className="p-field">
                                <label htmlFor="featured">Featured Artists</label>
                                <MultiSelect optionLabel="label" optionValue="value" value={this.state.song.featuredArtists} options={this.state.featuredArtists}
                                             appendTo={document.body} selectedItemTemplate={multiSelectSelectedItemTemplate}
                                             onChange={this.handleFeaturedArtistChange} />
                            </div>

                            <div className="p-field">
                                <label htmlFor="surname">Album</label>
                                <Dropdown value={this.state.song.album.id} options={this.state.albums}
                                          appendTo={document.body} onChange={this.handleAlbumChange}
                                          placeholder="Select an album" />
                            </div>

                        </Dialog>

                        <Dialog visible={this.state.deleteDialogVisible} style={{width: '450px'}} header="Confirm" modal footer={deleteSongDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="confirmation-content">
                                <i className="pi pi-exclamation-triangle p-mr-3" style={{fontSize: '2rem'}}/>
                                {this.state.song && <span>Are you sure you want to delete <b>{this.state.song.name}</b>?</span>}
                            </div>
                        </Dialog>

                    </div>
                </div>
            </div>
        );
    }
}
