import {Routes, Route, NavLink, useLocation, useNavigate } from 'react-router-dom'
import './App.css'
import {Home} from './components/Home'
import {About} from './components/About'
import {UserRegistration} from './components/UserRegistration'
import {Url} from './components/Url'
import { UserLogin } from './components/UserLogin'
import { UserLogout } from './components/UserLogout'
import {UserProfile} from './components/UserProfile'
import { BreadCrumbView } from './components/BreadCrumbView'
import { ExtendExpiration } from './components/ExtendExpiration'
import { FindShortUrl } from './components/FindShortUrl'
import { ModifyPath } from './components/ModifyPath'
import { ModifyPathWrapper } from './components/ModifyPathWrapper'
import { ExtendExpirationWrapper } from './components/ExtendExpirationWrapper'
import { 
  FileOutlined,
   UserOutlined, 
   DesktopOutlined,
    FileAddOutlined,
     LogoutOutlined,
     ProfileOutlined, 
     HomeOutlined,
     SearchOutlined,
    EditOutlined
   } from '@ant-design/icons';
import { Breadcrumb, Layout, Menu, theme } from 'antd';
import { useState } from 'react';
import { ProtectedRoutes } from './components/ProtectedRoutes'
import { postWithJwt } from './clients'
import { successNotification, errorNotification } from './Notification'


const pathToKey = {
  "/pages/user/url-edit-path": "/pages/user/edit-path",
  "/pages/user/url-extend": "/pages/user/extend"
}

const { Header, Content, Footer, Sider } = Layout;
function getItem(label, key, icon, children) {
  return {
    key,
    icon,
    children,
    label,
  };
}
const items = [
  getItem('Home', "/", <NavLink to="/"><HomeOutlined /></NavLink>),
  getItem('About', "/pages/About", <NavLink to="/pages/About"><DesktopOutlined /></NavLink>),
  getItem('User', '/pages/login', <UserOutlined />, [
    getItem('User Profile', "/pages/user", <NavLink to="/pages/user"><ProfileOutlined /></NavLink>),
    getItem('My URLs', "/pages/user/urls", <NavLink to="/pages/user/urls"><FileOutlined /></NavLink>),
    getItem('Edit Path', "/pages/user/edit-path", <NavLink to="/pages/user/edit-path"><EditOutlined /></NavLink>),
    getItem('Extend', "/pages/user/extend", <NavLink to="/pages/user/extend"><FileAddOutlined /></NavLink>),
    getItem('Find Short URL', "/pages/user/find-short-url", <NavLink to="/pages/user/find-short-url"><SearchOutlined /></NavLink>),
    getItem('Logout', "/pages/user/logout", <NavLink to="/pages/user/logout"><LogoutOutlined /></NavLink>),
    ])
];

function App() {
    const [collapsed, setCollapsed] = useState(false);
    const location = useLocation();
    const { pathname } = location;
    const navigate = useNavigate();

    //console.log("App(1): " + ((localStorage.getItem("jwt") !== null) ? jwt(localStorage.getItem("jwt")).sub : ""))
    // console.log("App(2): " + ((cookies.get("jwt_authentication") !== null) ? jwt(cookies.get("jwt_authentication")).sub : ""))

    function RedirectService(props) {
      console.log("Redirecting... " + props.state);
      const payload = { shortUrlPath: props.state}
      postWithJwt("/api/v1/user/urls/long", payload)
        .then(response => response.json())
        .then(data => {
          console.log(data);
          if (data.isActive){
            window.open(data.longUrl, "_self")
          } else {
            errorNotification("Redirect failed", "URL is inactive.")
            navigate('/');
          }
        }).catch(
          error => {
            console.log(error)
            if (localStorage.getItem("jwt") == null){
              errorNotification("Redirect failed", "URL not found")
            }
            else {
              errorNotification("Redirect failed", "URL not found for the user.");
            }
            navigate('/');
              })
            }


    const {
        token: { colorBgContainer },
    } = theme.useToken();
    const paths = window.location.pathname.split('/');
    if(paths.length == 2 && paths.at(1) !== ''){
      return <RedirectService state={paths.at(1)}/>
    }
    return (
        <Layout
          style={{
            minHeight: '100vh',
          }}
        >
          <Sider collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
            <div
              style={{
                height: 32,
                margin: 16,
                background: 'rgba(255, 255, 255, 0.2)',
              }}
            />
            <Menu theme="dark" selectedKeys={[pathname in pathToKey ? pathToKey[pathname] : pathname]} mode="inline" items={items} />

          </Sider>
          <Layout className="site-layout">
            <Header
              style={{
                padding: 0,
                background: colorBgContainer,
              }}
            />
            <Content
              style={{
                margin: '0 16px',
              }}
            >
              <Breadcrumb
                style={{
                  margin: '16px 0',
                }}
              >
              <BreadCrumbView />
              </Breadcrumb>
              <div
                style={{
                  padding: 24,
                  minHeight: 360,
                  background: colorBgContainer,
                }}
              >
             <Routes>
                 <Route path="/" element={<Home />}/>
                 <Route path="/pages/about" element={<About />}/>
                  <Route path="/pages/login" element={<UserLogin />}/>
                  <Route path="/pages/register" element={<UserRegistration/>}/>
                  <Route path="/pages/user/logout" element={<UserLogout/>}/>
                   <Route path="/:name" element={<RedirectService />}/>
                 <Route element={<ProtectedRoutes />}>
                    <Route path="/pages/user" element={<UserProfile />}/>
                    <Route path="/pages/user/urls" element={<Url />}/>
                    <Route path="/pages/user/find-short-url" element={<FindShortUrl />}/>
                    <Route path="/pages/user/extend" element={<ExtendExpirationWrapper />}/>
                    <Route path="/pages/user/url-extend" element={<ExtendExpiration />}/>
                    <Route path="/pages/user/edit-path" element={<ModifyPathWrapper />}/>
                    <Route path="/pages/user/url-edit-path" element={<ModifyPath />}/>
                 </Route>
             </Routes>
              </div>
            </Content>
            <Footer
              style={{
                textAlign: 'center',
              }}
            >
              By Jung Hoon Lim
            </Footer>
          </Layout>
        </Layout>
      );
}

export default App;