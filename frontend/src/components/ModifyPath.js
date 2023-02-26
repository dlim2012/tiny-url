
import {
    Button,
    Form,
    Input,
    Switch,
    Descriptions,
    Spin,
    Divider
  } from 'antd';

import { useState } from 'react';
import  { postWithJwt } from '../clients'
import { loadingIcon } from './utils';
import { successNotification, errorNotification } from '../Notification'

const { TextArea } = Input;

export const ModifyPath = () => {
    
    const [form] = Form.useForm();
    const [pathModificationResponse, setPathModificationResponse] = useState({});
    const [isToggled, setIsToggled] = useState(false);
    const [fetching, setFetching] = useState(false);

    const onFinish = (values) => {
        
        const isPrivate = (values.isPrivate == null ? localStorage.getItem("isPrivate") === "O" : values.isPrivate == false)
        setFetching(true);
        const payload = {
          newShortUrlPath: values.newShortUrlPath == null ? "": values.newShortUrlPath,
          longUrl: values.longUrl,
          isPrivate: isPrivate,
          newDescription: values.newDescription == null ? "" : values.newDescription,
        }
        postWithJwt("/api/v1/user/urls/modify", payload)
          .then(response => response.json())
          .then(data =>{
              setPathModificationResponse(data);
              setIsToggled(true);
              successNotification("URL path modified", ``)
          }).catch (error => {
            console.log(error)
            setIsToggled(false)
            error.response.json().then(data => {
              console.log(data)
              errorNotification("Create new URL failed", `${data.message}`)
          })
          }).finally (() => {
            setFetching(false);
           })
    };

    if (fetching) {
        return <Spin indicator={loadingIcon} />
    }

    return (
        <>
        <br />
            <Form
              form={form}
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 14 }}
              layout="horizontal"
              style={{ maxWidth: 800 }}
              onFinish={onFinish}
            >
            <Form.Item
                label="Long URL"
                name="longUrl"
                initialValue={localStorage.getItem("longUrl")}
                rules={[{
                    required: true,
                    message: 'Long URL is required',
                  }]}
                >
              <Input placeholder="Enter an Original URL registered by this account (Required)"/>
            </Form.Item>
            <Form.Item
                label="Private URL"
                valuePropName="checked"
                name="isPrivate"
                >
                 <Switch defaultChecked={localStorage.getItem("isPrivate") === "O"} />
            </Form.Item>
             <Divider />
            <Form.Item
                label="New Path"
                name="newShortUrlPath"
                rules={[{
                    required: false,
                    message: 'A new Path for short URL is required',
                  },{
                    pattern: /^[a-zA-Z0-9\-\_]+$/,
                    message: 'Custom path can only include alphanumeric characters, underscore, and dash.'
                  },
                    {
                      pattern: /^[a-zA-Z0-9\-\_]{8,1000}$/,
                        message: "Custom path should be either empty or at least 8 characters"
                    },
                    {
                      pattern: /^[a-zA-Z0-9\-\_]{1,50}$/,
                        message: "Custom path should have length at most 50"
                    }
                  ]}
                >
              <Input placeholder="Enter a new path (Optional)"/>
            </Form.Item>
            <Form.Item
                label="New Note"
                name="newDescription"
                >
              <TextArea rows={6} placeholder="Enter new a note (Optional)"/>
            </Form.Item>
              <Form.Item
                  wrapperCol={{
                  offset: 4,
                  span: 16,
                  }}
              >
              <Button htmlType="submit">Submit</Button>
              </Form.Item>
            </Form>
            { isToggled && <Descriptions title="Modification result" bordered>
              <Descriptions.Item label="Previous Short URL">{pathModificationResponse.prevShortUrl}</Descriptions.Item>
              <Descriptions.Item label="New Short URL">{pathModificationResponse.newShortUrl}</Descriptions.Item>
                <Descriptions.Item label="Expire Date">{pathModificationResponse.expireDate}</Descriptions.Item>
            </Descriptions> 
        }
            </>
        );
}
