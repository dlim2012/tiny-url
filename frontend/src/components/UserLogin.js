import 'bootstrap/dist/css/bootstrap.min.css';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import Cookies from "universal-cookie";
import jwt from "jwt-decode"
import { Button, Form, Input } from 'antd';
import { successNotification, errorNotification } from '../Notification'
import { post } from '../clients'



export function UserLogin () {

    const navigate = useNavigate();
    const cookies = new Cookies();
    const location = useLocation();

  const onFinish = (values) => {
    const payload = {
        email: values.username,
        password: values.password
      }
    post("/api/v1/auth/login", payload)
    .then(response => response.json())
      .then(data => {
        console.log(data)
        const jwt_authentication = data.token;
        const decoded = jwt(jwt_authentication)
        cookies.set("jwt_authentication", jwt_authentication, {
          expires: new Date(decoded.exp * 1000)
        });
        console.log("User logged in");
        successNotification("User logged in")
        localStorage.setItem('jwt', jwt_authentication);
        navigate(location.state.from);
      }
      ). catch (error => {
      console.log(error)
      error.response.json().then(data => {
        console.log(data);
        errorNotification("User login failed", `${data.message}`);
      })
    })
  }
    
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
          label="Email"
          name="username"
          rules={[{ required: true, message: 'Please input your email!' }]}
        >
          <Input placeholder='Enter your email address'/>
        </Form.Item>
        <Form.Item
          label="Password"
          name="password"
          rules={[{ required: true, message: 'Please input your password!' }]}
        >
          <Input.Password placeholder='Enter your password'/>
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

