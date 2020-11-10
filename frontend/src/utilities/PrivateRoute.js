import React from 'react';
import {Redirect, Route} from "react-router-dom";

const PrivateRoute = ({component: Component, user, authenticated, ...rest}) => (

    <Route
        {...rest}
        render={props =>

            authenticated ? (
                <Component user={user} {...rest} {...props} />
            ) : (
                <Redirect
                    to={{
                        pathname: '/login',
                        state: {from: props.location}
                    }}
                />
            )
        }
    />
);

export default PrivateRoute
