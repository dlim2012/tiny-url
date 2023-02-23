
import {
    Button,
    Form,
    Input,
    Switch,
    Empty,
    Descriptions,
    Spin
  } from 'antd';

import { useState } from 'react';
import Cookies from "universal-cookie";
import  { getExtensionUrlResponse } from '../clients'
import { loadingIcon } from './utils';



export const ExtendExpiration = () => {
    
    const [form] = Form.useForm();
    const [extensionResponse, setExtensionResponse] = useState({});
    const [isToggled, setIsToggled] = useState(false);
    const [fetching, setFetching] = useState(false);
    const cookies = new Cookies();

    const onFinish = (values) => {
        setFetching(true);
        getExtensionUrlResponse(cookies.get("jwt_authentication"), 
        {
            number: 1,
            shortUrl: values.shortUrl,
            isPrivate: values.isPrivate == null ? false : true
        })
        .then(response => response.json())
        .then(data =>{
            setExtensionResponse(data);
            setIsToggled(true);
        })
        setFetching(false);
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
                label="Short URL"
                name="shortUrl"
                rules={[{
                    required: true,
                    message: 'Short URL is required',
                  }]}
                >
              <Input placeholder="Enter a Short URL (Required)"/>
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