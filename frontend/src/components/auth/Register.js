import React, {Component} from 'react';
import {InputText} from 'primereact/inputtext';
import {Button} from 'primereact/button';
import {Panel} from 'primereact/panel';
import {ACCESS_TOKEN} from "../../Constants";
import {handleRegistration} from "../../utilities/JWTAuth";
import {Toast} from "primereact/toast";

export class Register extends Component {

    constructor(props) {
        super(props);

        this.state = {
            name: '',
            surname: '',
            email: '',
            password: ''
        };


        this.handleNameChange = this.handleNameChange.bind(this);
        this.handleSurnameChange = this.handleSurnameChange.bind(this);
        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleNameChange(event) {
        this.setState({name: event.target.value});
    }

    handleSurnameChange(event) {
        this.setState({surname: event.target.value});
    }

    handleEmailChange(event) {
        this.setState({email: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit = (event) => {
        event.preventDefault();

        const registerRequest = {
            'name': this.state.name,
            'surname': this.state.surname,
            'email': this.state.email,
            'password': this.state.password,
        };

        if (registerRequest.name.length === 0) {
            return;
        }

        if (registerRequest.surname.length === 0) {
            return;
        }

        if (registerRequest.email.length === 0) {
            return;
        }

        if (registerRequest.password.length === 0) {
            return;
        }

        handleRegistration(registerRequest)
            .then(r => {
                console.log(r);
                localStorage.setItem(ACCESS_TOKEN, r.object.accessToken);
                this.props.onLogin(r);
            }).catch(error => {
                this.toast.show({severity:'error', detail: error.message, life: 3000});
            });



        this.setState({password: ''});
    };

    render() {
        const styles = {
            position: 'absolute',
            left: '50%',
            top: '50%',
            transform: 'translate(-50%, -50%)',
            padding: '10px'
        };

        return (
            <div>
                <Toast ref={(el) => this.toast = el} />
                <Panel className="loginPanel" header="Register" style={styles}>

                    <form className="onboard-form" onSubmit={this.handleSubmit}>
                        <div className="p-grid p-fluid">
                            <div className="p-col-12 p-lg-12">
                                <div className="p-col-12">
                                    <div className="p-grid">

                                        <div className="p-col-12 p-md-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon"><i className="pi pi-user" /></span>
                                                <InputText type="text" value={this.state.name} onChange={this.handleNameChange} placeholder="Name" />
                                            </div>
                                        </div>

                                        <div className="p-col-12 p-md-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon"><i className="pi pi-user" /></span>
                                                <InputText type="text" value={this.state.surname} onChange={this.handleSurnameChange} placeholder="Surname" />
                                            </div>
                                        </div>

                                        <div className="p-col-12 p-md-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon"><i className="pi pi-briefcase" /></span>
                                                <InputText type="email" value={this.state.email} onChange={this.handleEmailChange} placeholder="Email address" />
                                            </div>
                                        </div>

                                        <div className="p-col-12 p-md-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon"><i className="pi pi-key" /></span>
                                                <InputText type="password" value={this.state.password} onChange={this.handlePasswordChange} placeholder="Password" />
                                            </div>
                                        </div>

                                        <div className="p-col-12 p-md-12">
                                            <Button type="submit" label="Register" className="p-button-info" style={{width:'8em', marginRight:'.25em'}} />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </Panel>
            </div>
        );
    }
}
