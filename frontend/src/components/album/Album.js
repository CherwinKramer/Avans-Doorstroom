import React, {Component} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {Toolbar} from 'primereact/toolbar';
import {createAlbum, deleteAlbum, getAlbum, getAlbums, getSongsForAlbum, updateAlbum} from "../../service/AlbumService";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {getAlbumsForArtist, getArtists} from "../../service/ArtistService";
import {Dropdown} from "primereact/dropdown";
import {Toast} from "primereact/toast";

export class Album extends Component {

    constructor(props) {
        super(props);

        this.state = {
            album: {
                id: '',
                name: '',
                artist: {
                    id: ''
                },
            },
            songs: [],
            albums: [],
            artists: [],
            dialogVisible: false,
            songDialogVisible: false,
            deleteDialogVisible: false
        };

        this.openEditDialog = this.openEditDialog.bind(this);
        this.resetValues = this.resetValues.bind(this);
        this.saveAlbum = this.saveAlbum.bind(this);
        this.deleteAlbum = this.deleteAlbum.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleArtistChange = this.handleArtistChange.bind(this);
    }

    componentDidMount() {
        getAlbums().then(albumData => {
            this.setState({
                albums: albumData.object
            })
            getArtists().then(artistData => {
                let artistSelectListData = artistData.object.map(artist => { return {label: artist.name + ' ' + artist.surname, value: artist.id} });
                this.setState({artists: artistSelectListData});
            })
        });
    }

    openEditDialog(rowData) {
        getAlbum(rowData.id).then(albumData => {
            this.setState({
                album: albumData.object,
                dialogVisible: true
            });
        });
    }

    resetValues() {
        this.setState({
            album: {
                id: '',
                name: '',
                artist: {
                    id: '',
                    name: '',
                    surname: ''
                },
            },
            songs: [],
            dialogVisible: false,
            songDialogVisible: false,
            deleteDialogVisible: false
        });
    }

    findSongsByAlbum(album) {
        getSongsForAlbum(album.id).then(data => {
            this.setState({
                songs: data.object,
                songDialogVisible: true
            })
        });
    }

    handleNameChange(event) {
        this.state.album['name'] = event.target.value;
    }

    handleArtistChange(event) {
        let album = this.state.album;
        album['artist'].id = event.value;
        this.setState({album: album});
    }

    saveAlbum() {
        let album = this.state.album;
        let albums = this.state.albums;
        let creation = !album.id;

        let failed = false;
        if (!album.name) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Name must be filled in', life: 3000});
            failed = true;
        }
        if (!album.artist.id) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Artist must be selected', life: 3000});
            failed = true;
        }
        if (failed) return;

        if (creation) {
            createAlbum(album).then(r => {
                albums.push(r.object);
                this.setState({albums: albums});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        } else {
            updateAlbum(album).then(r => {
                albums[this.findSelectedAlbumIndex()] = album;
                this.setState({albums:albums});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        }
    }

    deleteAlbum() {
        let album = this.state.album;
        let albums = this.state.albums;
        let indexId = albums.map(x => x.id).indexOf(album.id);

        deleteAlbum(album).then(r => {
            albums.splice(indexId, 1);
            this.setState({albums: albums});
            this.resetValues();
            this.toast.show({severity:'success', detail: 'Album has been successfully deleted', life: 3000});
        }).catch(e => {
            this.toast.show({severity:'error', detail: 'Could not delete the album', life: 3000});
        });
    }

    findSelectedAlbumIndex() {
        return this.state.albums.map(x => x.id).indexOf(this.state.album.id);
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
                    <Button icon="pi pi-trash" className="p-button-rounded p-button-danger p-mr-2" onClick={() => {this.setState({deleteDialogVisible: true, album: rowData})}}/>
                    <Button icon="pi pi-briefcase" className="p-button-rounded p-button-info" onClick={() => {this.findSongsByAlbum(rowData)}}/>
                </div>
            );
        }

        const albumDialogFooter = (
            <>
                <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={() => {this.setState({dialogVisible: false})}}/>
                <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={() => this.saveAlbum}/>
            </>
        );

        const deleteAlbumDialogFooter = (
            <>
                <Button label="No" icon="pi pi-times" className="p-button-text" onClick={() => {this.resetValues()}}/>
                <Button label="Yes" icon="pi pi-check" className="p-button-text" onClick={() => {this.deleteAlbum()}}/>
            </>
        );

        return (
            <div className="p-grid crud-demo">
                <Toast ref={(el) => this.toast = el} />

                <div className="p-col-12">
                    <div className="card">
                        <Toolbar className="p-mb-4" left={leftToolbarTemplate} />

                        <DataTable value={this.state.albums}
                                   dataKey="id" paginator rows={10} rowsPerPageOptions={[5, 10, 25]} className="p-datatable"
                                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                   currentPageReportTemplate="Showing {first} to {last} of {totalRecords} products"
                                   emptyMessage="No albums found." header="Manage albums">

                            <Column field="name" header="Name" sortable />
                            <Column field="artist.fullName" header="artist" sortable />
                            <Column body={actionBodyTemplate} />

                        </DataTable>

                        <Dialog visible={this.state.dialogVisible} style={{width: '450px'}} header="Album" modal className="p-fluid" footer={albumDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="p-field">
                                <label htmlFor="name">Name</label>
                                <InputText id="name" defaultValue={this.state.album.name} onChange={(e) => this.state.album['name'] = e.target.value} required autoFocus />
                            </div>

                            <div className="p-field">
                                <label htmlFor="surname">Artist</label>
                                <Dropdown value={this.state.album.artist.id} options={this.state.artists}
                                          appendTo={document.body} onChange={this.handleArtistChange}
                                          placeholder="Select an artist" />
                            </div>
                        </Dialog>

                        <Dialog visible={this.state.deleteDialogVisible} style={{width: '450px'}} header="Confirm" modal footer={deleteAlbumDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="confirmation-content">
                                <i className="pi pi-exclamation-triangle p-mr-3" style={{fontSize: '2rem'}}/>
                                {this.state.album && <span>Are you sure you want to delete <b>{this.state.album.name}</b>?</span>}
                            </div>
                        </Dialog>

                        <Dialog visible={this.state.songDialogVisible} style={{width: '1000px'}} header="Songs" modal onHide={() => {this.resetValues()}}>
                            <DataTable value={this.state.songs} paginator rows={10}
                                       paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                       emptyMessage="No songs found." className="p-datatable" >

                                <Column field="name" header="Name" sortable />
                                <Column field="artist.fullName" header="Artist" sortable />
                                <Column field="featuredArtistNames" header="Featured Artists" />
                                <Column field="genre.name" header="Genre" />

                            </DataTable>
                        </Dialog>

                    </div>
                </div>
            </div>
        );
    }
}
