import { Outlet, Navigate, useLocation } from "react-router-dom";


export const ProtectedRoutes = (jwtAuth) =>{
    const location = useLocation();
    return jwtAuth.jwtAuth === "" ?
         <Navigate to="/pages/login"  replace state={{from: location}}/> 
         : <Outlet />;
}

// export default ProtectedRoutes;

