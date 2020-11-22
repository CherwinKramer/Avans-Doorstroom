import React, {Component} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {Toolbar} from 'primereact/toolbar';
import {createArtist, deleteArtist, getAlbumsForArtist, getArtist, getArtists, getSongsForArtist, updateArtist} from "../../service/ArtistService";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Toast} from "primereact/toast";
import {DataScroller} from "primereact/datascroller";

export class Artist extends Component {

    constructor(props) {
        super(props);

        this.state = {
            artist: {
                id: '',
                name: '',
                surname: '',
                place: ''
            },
            artists: [],
            dialogVisible: false,
            deleteDialogVisible: false,
            albumDialogVisible: false
        };

        this.openEditDialog = this.openEditDialog.bind(this);
        this.findAlbumsByArtist = this.findAlbumsByArtist.bind(this);
        this.resetValues = this.resetValues.bind(this);
        this.saveArtist = this.saveArtist.bind(this);
        this.deleteArtist = this.deleteArtist.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleSurnameChange = this.handleSurnameChange.bind(this);
        this.handlePlaceChange = this.handlePlaceChange.bind(this);

    }

    componentDidMount() {
        getArtists().then(data => {
            this.setState({
                artists: data.object
            })
        });
    }

    resetValues() {
        this.setState({
            artist: {
                id: '',
                name: '',
                surname: '',
                place: ''
            },
            dialogVisible: false,
            deleteDialogVisible: false,
            albumDialogVisible: false,
            expandedRows: []
        });
    }

    handleNameChange(event) {
        this.state.artist['name'] = event.target.value;
    }

    handleSurnameChange(event) {
        this.state.artist['surname'] = event.target.value;
    }

    handlePlaceChange(event) {
        this.state.artist['place'] = event.target.value;
    }

    findAlbumsByArtist(artist) {
        getAlbumsForArtist(artist.id).then(data => {
            this.setState({
                albums: data.object,
                albumDialogVisible: true
            })
        });

        getSongsForArtist(artist.id).then(data => {
            this.setState({
                songs: data.object,
                albumDialogVisible: true
            })
        });

    }

    openEditDialog(rowData) {
        getArtist(rowData.id).then(artistData => {
            console.log(artistData);
            this.setState({
                artist: artistData.object,
                dialogVisible: true
            });
        });
    }

    saveArtist() {
        let artist = this.state.artist;
        let artists = this.state.artists;
        let creation = !artist.id;

        let failed = false;
        if (!artist.name) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Name must be filled in', life: 3000});
            failed = true;
        }
        if (!artist.surname) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Surname must be filled in', life: 3000});
            failed = true;
        }
        if (!artist.place) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Place must be filled in', life: 3000});
            failed = true;
        }
        if (failed) return;

        if (creation) {
            createArtist(artist).then(r => {
                artists.push(r.object);
                this.setState({artists: artists});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        } else {
            updateArtist(artist).then(r => {
                artists[this.findSelectedArtistIndex()] = artist;
                this.setState({artists:artists});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        }
    }

    deleteArtist() {
        let artist = this.state.artist;
        let artists = this.state.artists;
        let indexId = artists.map(x => x.id).indexOf(artist.id);

        deleteArtist(artist).then(r => {
            artists.splice(indexId, 1);
            this.setState({artists: artists});
            this.resetValues();
            this.toast.show({severity:'success', detail: r.message, life: 3000});
        }).catch(e => {
            this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
        });
    }

    findSelectedArtistIndex() {
        return this.state.artists.map(x => x.id).indexOf(this.state.artist.id);
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
                    <Button icon="pi pi-pencil" className="p-button-rounded p-button-success p-mr-2" onClick={() => {this.openEditDialog(rowData)}} />
                    <Button icon="pi pi-trash" className="p-button-rounded p-button-danger p-mr-2" onClick={() => {this.setState({deleteDialogVisible: true, artist: rowData})}}/>
                    <Button icon="pi pi-briefcase" className="p-button-rounded p-button-info p-mr-2" onClick={() => {this.findAlbumsByArtist(rowData)}}/>
                </div>
            );
        }

        const artistDialogFooter = (
            <>
                <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={() => {this.setState({dialogVisible: false})}}/>
                <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={this.saveArtist}/>
            </>
        );

        const deleteArtistDialogFooter = (
            <>
                <Button label="No" icon="pi pi-times" className="p-button-text" onClick={() => {this.resetValues()}}/>
                <Button label="Yes" icon="pi pi-check" className="p-button-text" onClick={() => {this.deleteArtist()}}/>
            </>
        );

        const songItemTemplate = (data) => {
            if (data) {
                return (
                    <div className="p-grid">
                        <div className="p-col-4 p-m-3">
                            <div>{data.artist.fullName}</div>
                            <div>{data.name}</div>
                        </div>
                        <div className="p-col p-m-3">
                            <div>{data.featuredArtistNames}</div>
                        </div>
                        <div className="p-col-1 p-m-3">
                            {data.url && <Button icon="pi pi-play" className="p-button-rounded p-button-secondary" onClick={() => {
                                window.open(data.url, "_blank")
                            }}/>}
                        </div>
                    </div>
                );
            }
            return "";
        }

        const albumSongExpansionTemplate = (data) => {
            if (data) {
                return (
                    <div className="p-card">
                        <DataScroller value={data.songs} itemTemplate={songItemTemplate} rows={5} inline scrollHeight="400px" header="Songs" />
                    </div>
                );
            }
            return "";
        }

        return (
            <div className="p-grid crud-demo">
                <Toast ref={(el) => this.toast = el} />

                <div className="p-col-12">
                    <div className="card">
                        <Toolbar className="p-mb-4" left={leftToolbarTemplate} />

                        <DataTable value={this.state.artists}
                                   dataKey="id" paginator rows={10} rowsPerPageOptions={[5, 10, 25]} className="datatable-responsive"
                                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                   currentPageReportTemplate="Showing {first} to {last} of {totalRecords} artists"
                                   emptyMessage="No artists found." header="Manage Artists">

                            <Column field="name" header="Name" sortable />
                            <Column field="surname" header="Surname" />
                            <Column field="place" header="Place" sortable />
                            <Column body={actionBodyTemplate} />

                        </DataTable>

                        <Dialog visible={this.state.dialogVisible} style={{width: '450px'}} header="Artist" modal className="p-fluid" footer={artistDialogFooter} onHide={() => {this.resetValues()}}>

                            <div className="p-field">
                                <label htmlFor="name">Name</label>
                                <InputText id="name" defaultValue={this.state.artist.name} onChange={(e) => this.state.artist['name'] = e.target.value} required autoFocus />
                            </div>

                            <div className="p-field">
                                <label htmlFor="surname">Surname</label>
                                <InputText id="surname" defaultValue={this.state.artist.surname}
                                           onChange={(e) => this.state.artist['surname'] = e.target.value} required autoFocus />
                            </div>

                            <div className="p-field">
                                <label htmlFor="place">Place</label>
                                <InputText id="place" defaultValue={this.state.artist.place} onChange={(e) => this.state.artist['place'] = e.target.value} required autoFocus />
                            </div>

                        </Dialog>

                        <Dialog visible={this.state.deleteDialogVisible} style={{width: '450px'}} header="Confirm" modal footer={deleteArtistDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="confirmation-content">
                                <i className="pi pi-exclamation-triangle p-mr-3" style={{fontSize: '2rem'}}/>
                                {this.state.artist && <span>Are you sure you want to delete <b>{this.state.artist.name} {this.state.artist.surname}</b>?</span>}
                            </div>
                        </Dialog>

                        <Dialog visible={this.state.albumDialogVisible} style={{width: '1000px'}} header="Albums" modal onHide={() => {this.setState({albums: [], songs: [], albumDialogVisible: false, expandedRows: []})}}>
                            <div>
                                <DataTable value={this.state.albums} paginator rows={10} header="List of all albums"
                                           paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                           emptyMessage="No albums found." className="datatable-responsive" expandedRows={this.state.expandedRows}
                                           rowExpansionTemplate={albumSongExpansionTemplate} onRowToggle={(e) => this.setState({ expandedRows: e.data })}>
                                    <Column expander style={{ width: '3em' }} />
                                    <Column field="name" header="Name" sortable />
                                </DataTable>
                            </div>

                            <div className="p-mt-lg-5">
                                <DataTable value={this.state.songs} paginator rows={10} header="List of all songs"
                                           paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                           emptyMessage="No songs found." className="datatable-responsive">
                                    <Column field="name" header="Name" sortable />
                                    <Column field="featuredArtistNames" header="Featured Artists" />
                                </DataTable>
                            </div>
                        </Dialog>

                    </div>
                </div>
            </div>
        );
    }
}
