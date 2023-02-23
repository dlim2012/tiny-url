import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import { useNavigate, useLocation, createSearchParams } from 'react-router-dom';
import Cookies from "universal-cookie";
import jwt from "jwt-decode"
import { Button, Form, Input } from 'antd';

function makeid(length) {
  let result = '';
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-';
  const charactersLength = characters.length;
  let counter = 0;
  while (counter < length) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
    counter += 1;
  }
  return result;
}

export function UserRegistration ({jwtAuth, setJwtAuth}) {
  const navigate = useNavigate();
  const cookies = new Cookies();
  const [form] = Form.useForm();
  const location = useLocation();
  // const location = useLocation();

  const onFill = () => {
    const id = makeid(6)
    form.setFieldsValue({
      firstname: "firstname_"+id,
      lastname: "lastname_"+id,
      username: "username_" + id+ "@email.com",
      password: "password"
    });
  }

  const onFinish = (values) => {
    try {
      {
        axios.post("/api/v1/auth/register",
        {
          firstname: values.firstname,
          lastname: values.lastname,
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
          console.log("User registration successful.");
          navigate(location.state.from);
        }
        );

        
        // navigate('/');
      }
    }
    catch (error){
      console.log(error)
      console.log("User registration failed.")
    }
  };
  
  const onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);
  };

  return (
    <>
    <br />
      <br />
    <Form
      form={form}
      name="basic"
      labelCol={{ span: 8 }}
      wrapperCol={{ span: 16 }}
      style={{ maxWidth: 600 }}
      initialValues={{ remember: true }}
      onFinish={onFinish}
      onFinishFailed={onFinishFailed}
      autoComplete="off"
    >
      <Form.Item
        label="Firstname"
        name="firstname"
        rules={[{ required: true, message: 'Please input your first name!' }]}
      >
        <Input placeholder='Enter your first name'/>
      </Form.Item>
      <Form.Item
        label="Lastname"
        name="lastname"
        rules={[{ required: true, message: 'Please input your last name!' }]}
      >
        <Input placeholder='Enter your last name'/>
      </Form.Item>
      <Form.Item
        label="Username"
        name="username"
        rules={[{ required: true, message: 'Please input your username!' }]}
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
      <Button type="link" htmlType="button" onClick={onFill}>
        Fill form
      </Button>
      </Form.Item>
    </Form>
    
    </>
    );
}
export default UserRegistration;

