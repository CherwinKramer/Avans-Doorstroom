import React, {Component} from 'react';
import {Redirect, Route, Switch, withRouter} from 'react-router-dom';
import classNames from 'classnames';
import PrivateRoute from "./utilities/PrivateRoute";

import {CSSTransition} from "react-transition-group";
import {getCurrentUser} from "./utilities/JWTAuth";
import {ACCESS_TOKEN, MENU} from "./Constants";

import 'primereact/resources/themes/saga-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import 'prismjs/themes/prism-coy.css';
import './layout/layout.scss';
import './App.scss';

import {AppTopbar} from './AppTopbar';
import {AppFooter} from './AppFooter';
import {AppMenu} from './AppMenu';
import {AppProfile} from './AppProfile';
import {Login} from "./components/auth/Login";
import {Register} from "./components/auth/Register";
import {Artist} from './components/artist/Artist';
import {Album} from "./components/album/Album";
import {Song} from "./components/song/Song";
import {Genre} from "./components/Genre/Genre"

class App extends Component {

    constructor(props) {
        super(props);

        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: true, // default true, to prevent rending without user is loaded for private route.

            staticMenuInactive: false,
            mobileMenuActive: false,
            menuClick: false,
            sidebar: null
        }

        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
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
            'layout-static-sidebar-inactive': this.state.staticMenuInactive,
            'layout-mobile-sidebar-active': this.state.mobileMenuActive
        });
        const sidebarClassName = classNames("layout-sidebar", 'layout-sidebar-dark');

        const isDesktop = () => {
            return window.innerWidth > 1024;
        }

        const addClass = (element, className) => {
            if (element.classList)
                element.classList.add(className);
            else
                element.className += ' ' + className;
        }

        const removeClass = (element, className) => {
            if (element.classList)
                element.classList.remove(className);
            else
                element.className = element.className.replace(new RegExp('(^|\\b)' + className.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
        }

        if (this.state.mobileMenuActive) {
            addClass(document.body, 'body-overflow-hidden');
        } else {
            removeClass(document.body, 'body-overflow-hidden');
        }

        const onToggleMenu = (event) => {
            if (isDesktop()) {
                this.setState({
                    menuClick: true,
                    staticMenuInactive: !this.state.staticMenuInactive
                })
            } else {
                this.setState({
                    menuClick: true,
                    mobileMenuActive: !this.state.mobileMenuActive
                })
            }
            event.preventDefault();
        }

        const onWrapperClick = (event) => {
            if (!this.state.menuClick) {
                this.setState({
                    mobileMenuActive: false
                })
            }
            this.setState({
                menuClick: false,
            })
        }

        const isSidebarVisible = () => {
            if (isDesktop()) {
                return !this.state.staticMenuInactive
            }
            return true;
        }

        const onSidebarClick = () => {
            this.setState({
                menuClick: true
            });
        }

        const onMenuItemClick = (event) => {
            if (!event.item.items) {
                this.setState({
                    mobileMenuActive: false
                })
            }
        }

        return (

            <div className={wrapperClass} onClick={onWrapperClick}>
                <AppTopbar onToggleMenu={onToggleMenu} logout={this.handleLogout}/>

                <CSSTransition classNames="layout-sidebar" timeout={{ enter: 200, exit: 200 }} in={isSidebarVisible()} unmountOnExit>
                    <div ref={this.state.sidebar} className={sidebarClassName} onClick={onSidebarClick}>
                        <AppProfile user={this.state.currentUser} logout={this.handleLogout}/>
                        <AppMenu user={this.state.currentUser} model={this.menu} onMenuItemClick={onMenuItemClick} />
                    </div>
                </CSSTransition>

                <div className="layout-main">
                    <PrivateRoute path="/" exact component={Song} user={this.state.currentUser}
                                  authenticated={this.state.isAuthenticated}/>

                    <PrivateRoute path="/dashboard" component={Song} user={this.state.currentUser}
                                  authenticated={this.state.isAuthenticated}/>

                    <PrivateRoute path="/artist" component={Artist} user={this.state.currentUser}
                                  authenticated={this.state.isAuthenticated}/>

                    <PrivateRoute path="/album" component={Album} user={this.state.currentUser}
                                  authenticated={this.state.isAuthenticated}/>

                    <PrivateRoute path="/song" component={Song} user={this.state.currentUser}
                                  authenticated={this.state.isAuthenticated}/>

                    <PrivateRoute path="/genre" component={Genre} user={this.state.currentUser}
                                  authenticated={this.state.isAuthenticated}/>
                </div>

                <AppFooter />

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

