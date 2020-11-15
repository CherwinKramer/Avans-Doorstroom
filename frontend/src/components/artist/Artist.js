import React, {Component} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {Toolbar} from 'primereact/toolbar';
import {createArtist, deleteArtist, getArtists, updateArtist} from "../../service/ArtistService";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Toast} from "primereact/toast";

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
            artists: {},
            dialogVisible: false,
            deleteDialogVisible: false
        };

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
            deleteDialogVisible: false
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

    saveArtist() {
        let artist = this.state.artist;
        let artists = this.state.artists;
        let creation = !artist.id;
        console.log('creation ' + creation);

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
                console.log(e);
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
            // notifier.success("Country has been successfully deleted");
        }).catch(e => {
            // notifier.error(e.error || "Error: Could not delete this country.");
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

        const nameBodyTemplate = (rowData) => {
            return (
                <>
                    {rowData.name}
                </>
            );
        }

        const surnameBodyTemplate = (rowData) => {
            return (
                <>
                    {rowData.surname}
                </>
            );
        }

        const placeBodyTemplate = (rowData) => {
            return (
                <>
                    {rowData.place}
                </>
            );
        }

        const actionBodyTemplate = (rowData) => {
            return (
                <div className="actions">
                    <Button icon="pi pi-pencil" className="p-button-rounded p-button-success p-mr-2" onClick={() => {this.setState({dialogVisible: true, artist: rowData})}}/>
                    <Button icon="pi pi-trash" className="p-button-rounded p-button-warning" onClick={() => {this.setState({deleteDialogVisible: true, artist: rowData})}}/>
                </div>
            );
        }

        // const albumDialogFooter = (
        //     <>
        //         <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={() => {this.setState({albumDialogVisible: false})}}/>
        //         {/*<Button label="Save" icon="pi pi-check" className="p-button-text" onClick={this.saveArtist}/>*/}
        //     </>
        // );

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

        return (
            <div className="p-grid crud-demo">
                <Toast ref={(el) => this.toast = el} />

                <div className="p-col-12">
                    <div className="card">
                        <Toolbar className="p-mb-4" left={leftToolbarTemplate} />

                        <DataTable value={this.state.artists}
                                   dataKey="id" paginator rows={10} rowsPerPageOptions={[5, 10, 25]} className="datatable-responsive"
                                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                   currentPageReportTemplate="Showing {first} to {last} of {totalRecords} products"
                                   emptyMessage="No artists found." header="Manage Artists">

                            <Column field="name" header="Name" sortable body={nameBodyTemplate} />
                            <Column field="surname" header="Surname" sortable body={surnameBodyTemplate} />
                            <Column field="place" header="Place" sortable body={placeBodyTemplate} />
                            <Column body={actionBodyTemplate} />

                        </DataTable>

                        {/*<Dialog visible={this.state.dialogVisible} style={{width: '450px'}} header="Artist" modal className="p-fluid" footer={artistDialogFooter} onHide={() => {this.resetValues()}}>*/}

                        {/*</Dialog>*/}

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
                                {this.state.artist && <span>Are you sure you want to delete <b>{this.state.artist.name}</b>?</span>}
                            </div>
                        </Dialog>

                    </div>
                </div>
            </div>
        );
    }
}
