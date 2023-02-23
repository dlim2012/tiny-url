import React from 'react'
import { useLocation, Link } from 'react-router-dom'
import { Breadcrumb } from 'antd'

const breadcrumbNames = {
    'url': 'URLs',
    'about': 'About',
    'user': 'User',
    'user-profile': 'User Profile',
    'login': 'Login',
    'generate': 'Generate',
    'find-short-url': 'Find Short URL'
}

const mapPath = (name) =>
    breadcrumbNames.hasOwnProperty(name) ? breadcrumbNames[name] : name;

export const BreadCrumbView = () => {
    const location = useLocation();
    const {pathname} = location;
    const pathnames = pathname.split("/").filter((item) => item);
    return (
        <div>
            <Breadcrumb>
                {pathnames.length > 0 ? (<Breadcrumb.Item>
                <Link to="/">Home</Link>
            </Breadcrumb.Item>
            ) : (
                <Breadcrumb.Item> Home </Breadcrumb.Item>
            )}
            {pathnames.map((name, index) => {
                if (name == 'pages') return;
                const routeTo = `/${pathnames.slice(0, index + 1).join("/")}`;
                const isLast = index === pathnames.length - 1;
                return isLast ? (
                    <Breadcrumb.Item>
                    {mapPath(name)}
                    </Breadcrumb.Item>
                ) : (<Breadcrumb.Item>
                    <Link to={`${routeTo}`}>{mapPath(name)}</Link>
                </Breadcrumb.Item>)
            })}
                            </Breadcrumb>
        </div>
    )
}

// export default BreadCrumbView;