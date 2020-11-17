import React, {Component} from 'react';
import PropTypes from 'prop-types';

export class AppTopbar extends Component {

    constructor(props) {
        super(props);
        this.onLogoutClick = this.onLogoutClick.bind(this);
    }

    onLogoutClick(event) {
        this.props.logout();
        event.preventDefault();
    }

    static defaultProps = {
        onToggleMenu: null
    };

    static propTypes = {
        onToggleMenu: PropTypes.func.isRequired
    };

    render() {
        return (
            <div className="layout-topbar clearfix">
                <button type="button" className="p-link layout-menu-button" onClick={this.props.onToggleMenu}>
                    <span className="pi pi-bars" />
                </button>
                <div className="layout-topbar-icons">
                    <button type="button" className="p-link" onClick={this.onLogoutClick}>
                        <span className="layout-topbar-item-text">Log off</span>
                        <span className="layout-topbar-icon pi pi-sign-out" />
                    </button>

                </div>
            </div>
        );
    }
}
