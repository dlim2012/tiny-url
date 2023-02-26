import { Outlet, Navigate, useLocation } from "react-router-dom";

export const ProtectedRoutes = () =>{
    const location = useLocation();
    return (localStorage.getItem('jwt') == null) ?
         <Navigate to="/pages/login"  replace state={{from: location}}/> 
         : <Outlet />;
}
