import React from 'react';
import Cookies from "universal-cookie";
import { useNavigate, Navigate } from 'react-router-dom';
import {Home} from './Home'

export function UserLogout({setJwtAuth}) {
    const cookies = new Cookies();
    cookies.remove("jwt_authentication");
    console.log("JWT removed to cookies")
    setJwtAuth("");
    return <div>Logged out</div>;
    // return <Navigate to="/" />;
}