
import axios from 'axios';
import Cookies from "universal-cookie";
import { useState } from 'react';

import { useNavigate, Navigate } from 'react-router-dom';
import {
    Button,
    Form,
    Input,
    Switch,
    Descriptions
  } from 'antd';

const { TextArea } = Input;


  const checkStatus = response => {
    console.log(response)
    if (response.ok) {
        return response;
    }
    const error = new Error(response.statusText);
    error.response = response;
    return Promise.reject(error);
}

export function UserGenerateUrl () {
    
    const [form] = Form.useForm();
    const navigate = useNavigate();
    const cookies = new Cookies();
    const [payload, setPayload] = useState({});
    const [isToggled, setIsToggled] = useState(false);
    const [response, setResponse] = useState({});

    const onFill = () => {
      form.setFieldsValue({
        longUrl: "https://github.com/dlim2012/tiny-url-system",
        description: "Link to codes for this project"
      });
    }

    const getResponse = (jwt_authentication, payload) => {
      const requestOptions = {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
              'Authorization': "Bearer " + jwt_authentication
          },
          body: JSON.stringify(payload)
      };
      return fetch("/api/v1/user/generate", requestOptions)
          .then(response => checkStatus(response))
  }

    const onFinish = (values) => {
        console.log('Received values of form: ', values);
        const payload = {
          shortUrlPath: values.shortUrlPath == null ? "" : values.shortUrlPath,
          longUrl: values.longUrl,
          isPrivate: values.isPrivate == null ? false : true,
          description: values.description == null ? "" : values.description
        }
        setPayload(payload);
        getResponse(cookies.get("jwt_authentication"), payload)
          .then(response => response.json())
          .then(data =>{
              setResponse(data);
              setIsToggled(true);
        });
      }

    return (
      <>
      <br />
          <Form
            form={form}
            labelCol={{ span: 4 }}
            wrapperCol={{ span: 14 }}
            layout="horizontal"
            style={{ maxWidth: 600 }}
            onFinish={onFinish}
          >
          <Form.Item
              label="Original URL"
              name="longUrl"
              rules={[{
                  required: true,
                  message: 'Original URL is required',
                }]}
              >
            <Input placeholder="Enter an Original URL (Required)"/>
          </Form.Item>
            <Form.Item
                label="Custom Path"
                name="shortUrlPath"
                rules={[
                    {
                      pattern: /^[a-zA-Z0-9\-\_]+$/,
                      message: 'Name can only include alphanumeric characters, underscore, and dash.',
                    },]}
            >
              <Input placeholder="Enter custom URL path (Optional)"/>
            </Form.Item>
            <Form.Item
                label="Description"
                name="description"
                >
              <TextArea rows={6} placeholder="Enter descriptions (Optional)"/>
            </Form.Item>
            <Form.Item
                label="Private URL"
                valuePropName="checked"
                name="isPrivate"
                >
              <Switch />
            </Form.Item>
            <Form.Item
                wrapperCol={{
                offset: 4,
                span: 16,
                }}
            >
            <Button htmlType="submit">Submit</Button>
        <Button type="link" htmlType="button" onClick={onFill}>
          Fill form
        </Button>
            </Form.Item>
          </Form>
          { isToggled &&
            <Descriptions title="Generated URL" bordered>
            <Descriptions.Item label="Generated">{response.isGenerated ? "True" : "False"}</Descriptions.Item>
              <Descriptions.Item label="Original URL">{payload.longUrl}</Descriptions.Item>
                <Descriptions.Item label="Short URL">{response.shortUrl}</Descriptions.Item>
                <Descriptions.Item label="Private">{response.isPrivate ? "True" : "False"}</Descriptions.Item>
                <Descriptions.Item label="Active">{response.isActive ? "True" : "False"}</Descriptions.Item>
                <Descriptions.Item label="Expire Date">{response.expireDate}</Descriptions.Item>
            </Descriptions>
            }
          </>
      );
}