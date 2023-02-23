import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import { NavLink, useNavigate, useLocation, createSearchParams } from 'react-router-dom';
import Cookies from "universal-cookie";
import jwt from "jwt-decode"
import { Button, Checkbox, Form, Input } from 'antd';



export function UserLogin ({jwtAuth, setJwtAuth}) {


    const navigate = useNavigate();
    const cookies = new Cookies();
    const location = useLocation();
  

    const onFinish = (values) => {
      try {
          axios.post("/api/v1/auth/login",
          {
            email: values.username,
            password: values.password
          }
          ).then(response => {
            const jwt_authentication = response.headers.jwt_authentication;
            setJwtAuth(jwt_authentication);
            const decoded = jwt(jwt_authentication)
            cookies.set("jwt_authentication", response.headers.jwt_authentication, {
              expires: new Date(decoded.exp * 1000)
            });
            console.log("JWT added to cookies")
            console.log("User login successful");
            console.log(location.state);
            navigate(location.state.from.pathname);
            // navigate({
            //   pathname: location.state.from.pathname,
            //   search: createSearchParams({ jwt: jwt_authentication }).toString()
            // });
          }
          );
      }
      catch (error){
        console.log("user login failed. " + error)
      }
    };
    
    const onFinishFailed = (errorInfo) => {
      console.log('Failed:', errorInfo);
    };


    const getPath = () => {
      return location.state?.from ? location.state.from.pathname : '/';
    }

    return (
      <>
      <Form
        name="basic"
        labelCol={{ span: 8 }}
        wrapperCol={{ span: 16 }}
        style={{ maxWidth: 600 }}
        initialValues={{ remember: true }}
        onFinish={onFinish}
        onFinishFailed={onFinishFailed}
        autoComplete="off"
      >
        <br />
        <br />
        <br />
        <Form.Item
          label="Username"
          name="username"
          rules={[{ required: true, message: 'Please input your username!' }]}
        >
          <Input placeholder='Enter email address'/>
        </Form.Item>
        <Form.Item
          label="Password"
          name="password"
          rules={[{ required: true, message: 'Please input your password!' }]}
        >
          <Input.Password placeholder='Enter password'/>
        </Form.Item>
    
        <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
          <Button type="primary" htmlType="submit">
            Submit 
          </Button> 
          &nbsp;&nbsp;&nbsp; <NavLink to="/pages/register" replace state={{from: getPath()}}>Create Account</NavLink>
        </Form.Item>
      </Form>
      </>
    );
}
export default UserLogin;

