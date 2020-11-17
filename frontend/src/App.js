import React, {Component} from 'react';
import classNames from 'classnames';
import {AppTopbar} from './AppTopbar';
import {AppFooter} from './AppFooter';
import {AppMenu} from './AppMenu';
import {AppProfile} from './AppProfile';
import {Redirect, Route, Switch, withRouter} from 'react-router-dom';
import {Dashboard} from './components/Dashboard';
import {Artist} from './components/artist/Artist';

import 'primereact/resources/themes/saga-orange/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import 'prismjs/themes/prism-coy.css';
import '@fullcalendar/core/main.css';
import '@fullcalendar/daygrid/main.css';
import '@fullcalendar/timegrid/main.css';
import './layout/flags/flags.css';
import './layout/layout.scss';
import './App.scss';
import {Login} from "./components/auth/Login";
import {Register} from "./components/auth/Register";
import {getCurrentUser} from "./utilities/JWTAuth";
import PrivateRoute from "./utilities/PrivateRoute";
import {ACCESS_TOKEN, MENU} from "./Constants";
import {CSSTransition} from "react-transition-group";
import {Album} from "./components/album/Album";
import {Song} from "./components/song/Song";
import {Genre} from "./components/Genre/Genre";

class App extends Component {

    constructor(props) {
        super(props);

        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: true, // default true, to prevent rending without user is loaded for private route.

            staticMenuInactive: false,
        }

        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
        this.handleLogin = this.handleLogin.bind(this);

        this.onToggleMenu = this.onToggleMenu.bind(this);
        this.onMenuItemClick = this.onMenuItemClick.bind(this);
        this.menu = MENU;
    }


    loadCurrentUser() {
        this.setState({
            isLoading: true
        });

        getCurrentUser().then(response => {
            if (this._isMounted) {
                this.setState({
                    currentUser: response,
                    isAuthenticated: true,
                    isLoading: false
                });
            }
        }).catch(error => {
            this.setState({
                isLoading: false
            });
        });
    }

    handleLogout(redirectTo = "/") {
        localStorage.removeItem(ACCESS_TOKEN);

        this.setState({
            currentUser: null,
            isAuthenticated: false
        });

        this.props.history.push(redirectTo);
    }

    handleLogin(response) {
        const currentUser = {
            name: response.object.name,
            email: response.object.email,
            surname: response.object.surname,
            id: response.object.id,
        };

        this.setState({
            currentUser: currentUser,
            isAuthenticated: true,
            isLoading: false
        }, () => {
            this.props.history.push("/");
        });
    }

    onMenuItemClick(event) {
    }

    onToggleMenu(event) {
        this.setState({
            staticMenuInactive: !this.state.staticMenuInactive
        });

        event.preventDefault();
    }

    LoginContainer = () => {
        return (
            <div className="container">
                <Route exact path="/" render={() => <Redirect to="/login"/>}/>
                <Route path="/login"
                       render={(props) => <Login onLogin={this.handleLogin} growl={this.growl} {...props} />}/>
                <Route path="/register"
                       render={(props) => <Register onLogin={this.handleLogin} growl={this.growl} {...props} />}/>
            </div>
        );
    };

    DefaultContainer = () => {
        const wrapperClass = classNames('layout-wrapper', {
            'layout-static': true,
            'layout-static-sidebar-inactive': this.state.staticMenuInactive
        });
        const sidebarClassName = classNames("layout-sidebar", 'layout-sidebar-dark');

        return (
            <div className={wrapperClass}>
                <AppTopbar onToggleMenu={this.onToggleMenu} logout={this.handleLogout}/>

                <CSSTransition classNames="layout-sidebar" timeout={{ enter: 200, exit: 200 }} in={!this.state.staticMenuInactive} unmountOnExit>
                    <div ref={(el) => this.sidebar = el} className={sidebarClassName}>
                        <AppProfile user={this.state.currentUser} logout={this.handleLogout}/>
                        <AppMenu user={this.state.currentUser} model={this.menu} onMenuItemClick={this.onMenuItemClick}/>
                    </div>
                </CSSTransition>

                <div className="layout-main">
                    <Switch>
                        <PrivateRoute path="/" exact component={Dashboard} user={this.state.currentUser}
                                      authenticated={this.state.isAuthenticated}/>

                        <PrivateRoute path="/dashboard" component={Dashboard} user={this.state.currentUser}
                                      authenticated={this.state.isAuthenticated}/>

                        <PrivateRoute path="/artist" component={Artist} user={this.state.currentUser}
                                      authenticated={this.state.isAuthenticated}/>

                        <PrivateRoute path="/album" component={Album} user={this.state.currentUser}
                                      authenticated={this.state.isAuthenticated}/>

                        <PrivateRoute path="/song" component={Song} user={this.state.currentUser}
                                      authenticated={this.state.isAuthenticated}/>

                        <PrivateRoute path="/genre" component={Genre} user={this.state.currentUser}
                                      authenticated={this.state.isAuthenticated}/>
                    </Switch>
                </div>

                <AppFooter/>

                <div className="layout-mask"/>
            </div>
        );
    };

    render() {
        if (!this.state.isAuthenticated) {
            if (localStorage.getItem(ACCESS_TOKEN)) {
                getCurrentUser().then(response => {
                    this.handleLogin(response);
                }).catch(error => {
                });
            }
        }

        return (
            <Switch>
                <Route exact path="/(login)" component={this.LoginContainer}/>
                <Route exact path="/(register)" component={this.LoginContainer}/>
                <PrivateRoute component={this.DefaultContainer} authenticated={this.state.isAuthenticated}/>
            </Switch>
        );
    }

}

export default withRouter(App);
