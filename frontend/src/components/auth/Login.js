import React, {Component} from 'react';
import {Link} from "react-router-dom";
import {InputText} from 'primereact/inputtext';
import {Button} from 'primereact/button';
import {Panel} from 'primereact/panel';
import {ACCESS_TOKEN} from "../../Constants";
import {handleLogin} from "../../utilities/JWTAuth";

export class Login extends Component {

    constructor(props) {
        super(props);

        this.state = {
            email: '',
            password: ''
        };


        this.handleEmailChange = this.handleEmailChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleEmailChange(event) {
        this.setState({email: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit = (event) => {
        event.preventDefault();

        const loginRequest = {
            'email': this.state.email,
            'password': this.state.password,
        };

        if (loginRequest.email.length === 0) {
            return
        }

        if (loginRequest.password.length === 0) {
            return
        }

        handleLogin(loginRequest)
            .then(response => {
                // notifier.success('You successfully logged in!');
                localStorage.setItem(ACCESS_TOKEN, response.object.accessToken);
                this.props.onLogin(response);

            })
            .catch(error => {
                if (error.status === 401) {
                    // notifier.error("Your email or password is incorrect. Please try again!");
                } else {
                    // notifier.error(error.message || 'Sorry! Something went wrong. Please try again!');
                }
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
                <Panel className="loginPanel" header="Log in" style={styles}>

                    <form className="onboard-form" onSubmit={this.handleSubmit}>
                        <div className="p-grid p-fluid">
                            <div className="p-col-12 p-lg-12">
                                <div className="p-col-12">
                                    <div className="p-grid">

                                        <div className="p-col-12 p-md-12">
                                            <div className="p-inputgroup">
                                                <span className="p-inputgroup-addon"><i className="pi pi-user" /></span>
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
                                            <Button type="submit" label="Log in" className="p-button-info" style={{width:'8em', marginRight:'.25em'}} />
                                            <Link to={{
                                                pathname: `/register`
                                            }}>
                                                <Button label="Register" className="p-button-secondary" style={{width:'8em', marginRight:'.25em'}} />
                                            </Link>
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
