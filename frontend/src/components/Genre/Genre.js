import React, {Component} from 'react';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';
import {Button} from 'primereact/button';
import {Toolbar} from 'primereact/toolbar';
import {createGenre, deleteGenre, getGenre, getGenres, updateGenre} from "../../service/GenreService";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Toast} from "primereact/toast";

export class Genre extends Component {

    constructor(props) {
        super(props);

        this.state = {
            genre: {
                id: '',
                name: ''
            },

            genres: [],
            dialogVisible: false,
            deleteDialogVisible: false
        };

        this.openEditDialog = this.openEditDialog.bind(this);
        this.resetValues = this.resetValues.bind(this);
        this.saveGenre = this.saveGenre.bind(this);
        this.deleteGenre = this.deleteGenre.bind(this);
        this.handleNameChange = this.handleNameChange.bind(this);
    }

    componentDidMount() {
        getGenres().then(genreData => {
            this.setState({
                genres: genreData.object
            })
        });
    }

    openEditDialog(rowData) {
        getGenre(rowData.id).then(genreData => {
            this.setState({
                genre: genreData.object,
                dialogVisible: true
            });
        });
    }

    resetValues() {
        this.setState({
            genre: {
                id: '',
                name: ''
            },
            dialogVisible: false,
            deleteDialogVisible: false
        });
    }

    handleNameChange(event) {
        this.state.genre['name'] = event.target.value;
    }

    saveGenre() {
        let genre = this.state.genre;
        let genres = this.state.genres;
        let creation = !genre.id;

        if (!genre.name) {
            this.toast.show({severity: 'error', summary: 'Validation error', detail: 'Name must be filled in', life: 3000});
            return;
        }

        if (creation) {
            createGenre(genre).then(r => {
                genres.push(r.object);
                this.setState({genres: genres});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        } else {
            updateGenre(genre).then(r => {
                genres[this.findSelectedGenreIndex()] = genre;
                this.setState({genres:genres});
                this.resetValues();
                this.toast.show({severity:'success', detail: r.message, life: 3000});
            }).catch(e => {
                this.toast.show({severity:'error', summary: e.error, detail:'An error occurred', life: 3000});
            });
        }
    }

    deleteGenre() {
        let genre = this.state.genre;
        let genres = this.state.genres;
        let indexId = genres.map(x => x.id).indexOf(genre.id);

        deleteGenre(genre).then(r => {
            genres.splice(indexId, 1);
            this.setState({genres: genres});
            this.resetValues();
            this.toast.show({severity:'success', detail: 'Genre has been successfully deleted', life: 3000});
        }).catch(e => {
            this.toast.show({severity:'error', detail: 'Could not delete the genre', life: 3000});
        });
    }

    findSelectedGenreIndex() {
        return this.state.genres.map(x => x.id).indexOf(this.state.genre.id);
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
                    <Button icon="pi pi-trash" className="p-button-rounded p-button-danger" onClick={() => {this.setState({deleteDialogVisible: true, genre: rowData})}}/>
                </div>
            );
        }

        const genreDialogFooter = (
            <>
                <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={() => {this.setState({dialogVisible: false})}}/>
                <Button label="Save" icon="pi pi-check" className="p-button-text" onClick={this.saveGenre}/>
            </>
        );

        const deleteGenreDialogFooter = (
            <>
                <Button label="No" icon="pi pi-times" className="p-button-text" onClick={() => {this.resetValues()}}/>
                <Button label="Yes" icon="pi pi-check" className="p-button-text" onClick={() => {this.deleteGenre()}}/>
            </>
        );

        return (
            <div className="p-grid">
                <Toast ref={(el) => this.toast = el} />

                <div className="p-col-12">
                    <div className="card">
                        <Toolbar className="p-mb-4" left={leftToolbarTemplate} />

                        <DataTable value={this.state.genres}
                                   dataKey="id" paginator rows={10} rowsPerPageOptions={[5, 10, 25]} className="datatable-responsive"
                                   paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
                                   currentPageReportTemplate="Showing {first} to {last} of {totalRecords} products"
                                   emptyMessage="No genres found." header="Manage genres">

                            <Column field="name" header="Name" sortable />
                            <Column body={actionBodyTemplate} />

                        </DataTable>

                        <Dialog visible={this.state.dialogVisible} style={{width: '450px'}} header="Genre" modal className="p-fluid" footer={genreDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="p-field">
                                <label htmlFor="name">Name</label>
                                <InputText id="name" defaultValue={this.state.genre.name} onChange={(e) => this.state.genre['name'] = e.target.value} required autoFocus />
                            </div>
                        </Dialog>

                        <Dialog visible={this.state.deleteDialogVisible} style={{width: '450px'}} header="Confirm" modal footer={deleteGenreDialogFooter} onHide={() => {this.resetValues()}}>
                            <div className="confirmation-content">
                                <i className="pi pi-exclamation-triangle p-mr-3" style={{fontSize: '2rem'}}/>
                                {this.state.genre && <span>Are you sure you want to delete <b>{this.state.genre.name}</b>?</span>}
                            </div>
                        </Dialog>

                    </div>
                </div>
            </div>
        );
    }
}
