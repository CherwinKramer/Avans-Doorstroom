import React, {Component} from 'react';
import classNames from 'classnames';
import {CSSTransition} from 'react-transition-group';

export class AppProfile extends Component {

    constructor(props) {
        super(props);
        this.state = {expanded: false};
        this.onClick = this.onClick.bind(this);
        this.onLogout = this.onLogout.bind(this);
    }

    onClick(event) {
        this.setState({expanded: !this.state.expanded});
        event.preventDefault();
    }

    onLogout(event) {
        this.props.logout();
        event.preventDefault();
    }

    render() {
        const name = this.props.user.name + " " + this.props.user.surname;

        return  (
            <div className="layout-profile">
                <div>
                    <img src="assets/layout/images/profile.png" alt="" />
                </div>
                <button className="p-link layout-profile-link" onClick={this.onClick}>
                    <span className="username">{ name }</span>
                    <i className="pi pi-fw pi-cog"/>
                </button>
                <CSSTransition classNames="p-toggleable-content" timeout={{ enter: 1000, exit: 450 }} in={this.state.expanded} unmountOnExit>
                    <ul className={classNames({ 'layout-profile-expanded': this.state.expanded })}>                        {/*<div>*/}
                        {/*    <Link to="/administration/account">*/}
                        {/*        <li><button className="p-link"> <i className="pi pi-fw pi-user"/><span>Account</span></button></li>*/}
                        {/*    </Link>*/}
                        {/*</div>*/}
                        <li><button className="p-link" onClick={this.onLogout}><i className="pi pi-fw pi-power-off"/><span>Logout</span></button></li>
                    </ul>
                </CSSTransition>
            </div>
        );
    }
}
