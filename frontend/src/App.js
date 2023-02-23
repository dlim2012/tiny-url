import {Routes, Route, NavLink} from 'react-router-dom'
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
import { 
  FileOutlined,
   UserOutlined, 
   DesktopOutlined,
    FileAddOutlined,
     LogoutOutlined,
     ProfileOutlined, 
     HomeOutlined,
     SearchOutlined } from '@ant-design/icons';
import { Breadcrumb, Layout, Menu, theme } from 'antd';
import { useState } from 'react';
import { ProtectedRoutes } from './components/ProtectedRoutes'

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
  getItem('Home', '1', <NavLink to="/"><HomeOutlined /></NavLink>),
  getItem('About', '2', <NavLink to="/pages/About"><DesktopOutlined /></NavLink>),
  getItem('User', 'sub1', <UserOutlined />, [
    getItem('User Profile', '7', <NavLink to="/pages/user"><ProfileOutlined /></NavLink>),
    getItem('My URLs', '8', <NavLink to="/pages/user/url"><FileOutlined /></NavLink>),
    getItem('Find Short URL', '3', <NavLink to="/pages/user/find-short-url"><SearchOutlined /></NavLink>),
    getItem('Extend', '10', <NavLink to="/pages/user/extend"><FileAddOutlined /></NavLink>),
    getItem('Logout', '9', <NavLink to="/pages/user/logout"><LogoutOutlined /></NavLink>),
    ])
];


function RedirectService(props) {
  console.log("Redirecting... " + props.state);
  window.open('http://localhost:8080/' + props.state, "_self")
}



function App() {
    const [collapsed, setCollapsed] = useState(false);
    const [jwtAuth, setJwtAuth] = useState("");

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
            <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline" items={items} />

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
                  {/* <Route path="/pages/order-summary" element={<OrderSumamry />}/> */}
                  <Route path="/pages/login" element={<UserLogin jwtAuth={jwtAuth} setJwtAuth={setJwtAuth} />}/>
                  <Route path="/pages/register" element={<UserRegistration jwtAuth={jwtAuth} setJwtAuth={setJwtAuth}/>}/>
                  <Route path="/pages/user/logout" element={<UserLogout setJwtAuth={setJwtAuth}/>}/>
                  // <Route path="/:name" element={<RedirectService />}/>
                 <Route element={<ProtectedRoutes jwtAuth={jwtAuth} setJwtAuth={setJwtAuth} />}>
                    <Route path="/pages/user" element={<UserProfile />}/>
                    <Route path="/pages/user/url" element={<Url />}/>
                    <Route path="/pages/user/find-short-url" element={<FindShortUrl />}/>
                    <Route path="/pages/user/extend" element={<ExtendExpiration />}/>
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