import React, {Component} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {Toolbar} from 'primereact/toolbar';
import {createAlbum, deleteAlbum, getAlbums, updateAlbum} from "../../service/AlbumService";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {getArtists} from "../../service/ArtistService";
import {Dropdown} from "primereact/dropdown";

export class Album extends Component {

    constructor(props) {
        super(props);

        this.state = {
            album: {
                id: '',
                name: '',
                artist: {
                    id: '',
                    name: '',
                    surname: ''
                },
            },

            albums: {},
            artists: {},
            dialogVisible: false,
            deleteDialogVisible: false
        };

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
                this.setState({
                    artists: artistData.object
                })
            })
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
            dialogVisible: false,
            deleteDialogVisible: false
        });
    }

    handleNameChange(event) {
        this.state.album['name'] = event.target.value;
    }

    handleArtistChange(event) {
        console.log(event.value);
        this.state.album.artist = event.value;
        console.log(this.state.album);
    }

    saveAlbum() {
        let album = this.state.album;
        let albums = this.state.albums;
        let creation = !album.id;
        console.log('creation ' + creation);

        if (!album.name || !album.surname || !album.place) {
            return;
        }

        if (creation) {
            createAlbum(album).then(r => {
                albums.push(r.object);
                this.setState({albums: albums});
                this.resetValues();
            }).catch(e => {
                // notifier.error(e.error || "Error.")
            });
        } else {
            updateAlbum(album).then(r => {
                albums[this.findSelectedAlbumIndex()] = album;
                this.setState({albums:albums});
                this.resetValues();
            }).catch(e => {
                // notifier.error(e.error || "Error.")
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
            // notifier.success("Country has been successfully deleted");
        }).catch(e => {
            // notifier.error(e.error || "Error: Could not delete this country.");
        });
    }

    findSelectedAlbumIndex() {
        return this.state.albums.map(x => x.id).indexOf(this.state.album.id);
    }

    render() {

        const artistMap = () => {
            let artistList = this.state.artists;
            console.log('artist map');
            console.log(artistList);
            // let test = artistList.map(artist => { return {label: artist.name, value: artist.id} });
            // console.log(test);

            let countryData = Object.keys(artistList).map(artist => { return {label: artist.name, value: artist.id} });
            return (countryData);
        }

        const leftToolbarTemplate = () => {
            return (
                <React.Fragment>
                    <Button label="New" icon="pi pi-plus" className="p-button-success p-mr-2" onClick={() => {this.setState({dialogVisible: true})}} />
                </React.Fragment>
            )
        }

        const nameBodyTemplate = (rowData) => {
            return (
                <>
                    {rowData.name}
                </>
            );
        }

        const artistBodyTemplate = (rowData) => {
            return (
                <>
                    {rowData.artist.name} {rowData.artist.surname}
                </>
            );
        }

        const actionBodyTemplate = (rowData) => {
            return (
                <div className="actions">
                    <Button icon="pi pi-pencil" className="p-button-rounded p-button-success p-mr-2" onClick={() => {this.setState({dialogVisible: true, album: rowData})}}/>
                    <Button icon="pi pi-trash" className="p-button-rounded p-button-warning" onClick={() => {this.setState({deleteDialogVisible: true, album: rowData})}}/>
                </div>
            );
        }

        const albumDialogFooter = (
            <>
                <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={() => {this.setState({dialogVisible: false})}}/>
                <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={this.saveAlbum}/>
            </>
        );

        const deleteAlbumDialogFooter = (
            <>
                <Button label="No" icon="pi pi-times" className="p-button-text" onClick={() => {this.resetValues()}}/>
                <Button label="Yes" icon="pi pi-check" className="p-button-text" onClick={() => {this.deleteAlbum()}}/>
            </>
        );

        const valueTemplate = (option, props) => {
            if (option) {
                return (
                    <>{option.name} {option.surname}</>
                );
            }

            return (
                <>{props.placeholder}</>
            );
        }

        const itemTemplate = (option) => {
            return (
                <>{option.name} {option.surname}</>
            );
        }

        return (
            <div className="p-grid crud-demo">
                <div className="p-col-12">
                    <div className="card">
                        <Toolbar className="p-mb-4" left={leftToolbarTemplate} />

                        <DataTable value={this.state.albums}
                                   dataKey="id" paginator rows={10} rowsPerPageOptions={[5, 10, 25]} className="datatable-responsive"
                                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                   currentPageReportTemplate="Showing {first} to {last} of {totalRecords} products"
                                   emptyMessage="No albums found." header="Manage albums">

                            <Column field="name" header="Name" sortable body={nameBodyTemplate} />
                            <Column field="artist" header="artist" sortable body={artistBodyTemplate} />
                            {/*<Column field="place" header="Place" sortable body={placeBodyTemplate} />*/}
                            <Column body={actionBodyTemplate} />

                        </DataTable>

                        <Dialog visible={this.state.dialogVisible} style={{width: '450px'}} header="Album" modal className="p-fluid" footer={albumDialogFooter} onHide={() => {this.resetValues()}}>

                            <div className="p-field">
                                <label htmlFor="name">Name</label>
                                <InputText id="name" defaultValue={this.state.album.name} onChange={(e) => this.state.album['name'] = e.target.value} required autoFocus />
                            </div>

                            <div className="p-field">
                                <label htmlFor="surname">Artist</label>
                                <Dropdown itemTemplate={itemTemplate} valueTemplate={valueTemplate} value={this.state.album.artist} options={this.state.artists}
                                          appendTo={document.body} onChange={this.handleArtistChange} placeholder="Select an artist" />
                            </div>

                        </Dialog>

                        <Dialog visible={this.state.deleteDialogVisible} style={{width: '450px'}} header="Confirm" modal footer={deleteAlbumDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="confirmation-content">
                                <i className="pi pi-exclamation-triangle p-mr-3" style={{fontSize: '2rem'}}/>
                                {this.state.album && <span>Are you sure you want to delete <b>{this.state.album.name}</b>?</span>}
                            </div>
                        </Dialog>

                    </div>
                </div>
            </div>
        );
    }
}
