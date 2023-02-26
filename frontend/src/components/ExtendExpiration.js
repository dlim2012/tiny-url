
import {
    Button,
    Form,
    Input,
    Switch,
    Descriptions,
    Spin
  } from 'antd';

import { useState } from 'react';
import  { postWithJwt } from '../clients'
import { loadingIcon } from './utils';
import { successNotification, errorNotification } from '../Notification'



export const ExtendExpiration = () => {
    
    const [form] = Form.useForm();
    const [extensionResponse, setExtensionResponse] = useState({});
    const [isToggled, setIsToggled] = useState(false);
    const [fetching, setFetching] = useState(false);

    const onFinish = (values) => {
        setFetching(true);
        const isPrivate = (values.isPrivate == null ? localStorage.getItem("isPrivate") === "O" : values.isPrivate == false)
    
        const payload = {
          number: 1,
          longUrl: values.longUrl,
          isPrivate: isPrivate
        }
            postWithJwt("/api/v1/user/urls/extend", payload)
            .then(response => response.json())
            .then(data =>{
                setExtensionResponse(data);
                setIsToggled(true);
                successNotification("URL expiration extended", ``)
            }).catch (error => {
            console.log(error)
            error.response.json().then(data => {
                console.log(data)
                errorNotification("Extend expiration failed", `${data.message}`)
            })}).finally ( () => {
            setFetching(false)
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
                label="Original URL"
                name="longUrl"
                initialValue={localStorage.getItem("longUrl")}
                rules={[{
                    required: true,
                    message: 'Registered original URL is required',
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
              <Form.Item
                  wrapperCol={{
                  offset: 4,
                  span: 16,
                  }}
              >
              <Button htmlType="submit">Submit</Button>
              </Form.Item>
            </Form>
            { isToggled && <Descriptions title="Extension result" bordered>
            <Descriptions.Item label="Extended">{extensionResponse.isExtended ? "True" : "False"}</Descriptions.Item>
              <Descriptions.Item label="Previous Expire Date">{extensionResponse.prevExpireDate}</Descriptions.Item>
                <Descriptions.Item label="New Expire Date">{extensionResponse.newExpireDate}</Descriptions.Item>
                <Descriptions.Item label="Available Short URLs">{extensionResponse.remainingNumber}</Descriptions.Item>
            </Descriptions> }
            </>
        );
}
