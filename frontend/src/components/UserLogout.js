import React from 'react';
import Cookies from "universal-cookie";
import { Navigate } from 'react-router-dom'
import { successNotification, errorNotification } from '../Notification'

export function UserLogout({setJwtAuth}) {
    const cookies = new Cookies();
    cookies.remove("jwt_authentication");
    localStorage.removeItem('jwt')

    successNotification("User logged out")
    return <Navigate to='/' />
}

