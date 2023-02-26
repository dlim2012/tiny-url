
import {
    Button,
    Form,
    Input,
    Empty,
    Descriptions,
    Spin
  } from 'antd';

import { useState } from 'react';
import  { postWithJwt } from '../clients'
import { loadingIcon } from './utils';
import { successNotification, errorNotification } from '../Notification'


export const FindShortUrl = () => {
    
    const [shortUrls, setShortUrls] = useState({});
    const [isToggled, setIsToggled] = useState(false);
    const [hasPublic, setHasPublic] = useState(false);
    const [hasPrivate, setHasPrivate] = useState(false);
    const [fetching, setFetching] = useState(false);

    const onFinish = (values) => {
        setFetching(true);
        const payload = { longUrl: values.longUrl }
          postWithJwt("/api/v1/shorturl/short", payload)
          .then(response => response.json())
          .then(data =>{
            console.log(data)
              setShortUrls(data);
              setHasPublic(data.publicShortUrl !== '');
              setHasPrivate(data.privateShortUrl !== '');
              setIsToggled(true);
          }).catch (error => {
            console.log(error)
            error.response.json().then(data => {
                console.log(data)
                errorNotification("Extend expiration failed", `${data.message}`)
            })}).finally ( () => {
            setFetching(false)
            })
          }

      if (fetching) {
        return <Spin indicator={loadingIcon} />
    }

    const publicUrl = <Descriptions title="My URLs" bordered>
        <Descriptions.Item label="Short URL">{shortUrls.publicShortUrl}</Descriptions.Item>
        <Descriptions.Item label="Description">{shortUrls.publicDescription}</Descriptions.Item>
        <Descriptions.Item label="Expire Date">{shortUrls.publicDescription}</Descriptions.Item>
        <Descriptions.Item label="Private">{shortUrls.publicDescription}</Descriptions.Item>
        <Descriptions.Item label="Active">{shortUrls.publicDescription}</Descriptions.Item>
    </Descriptions>

    const privateUrl = <Descriptions bordered>
    <Descriptions.Item label="Private Short URL">{shortUrls.privateShortUrl}</Descriptions.Item>
    <Descriptions.Item label="Private Description">{shortUrls.privateDescription}</Descriptions.Item>
    </Descriptions>

    return (
        <>
        <br />
          <Form
            labelCol={{ span: 4 }}
            wrapperCol={{ span: 14 }}
            layout="horizontal"
            style={{ maxWidth: 800 }}
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
            <Input placeholder="Enter an Original URL registered by this account (Required)"/>
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
            { isToggled && !hasPrivate && !hasPublic && <Empty/>}

          { isToggled && hasPublic && <Descriptions title="Public URL" bordered>
        <Descriptions.Item label="Short URL">{shortUrls.publicShortUrl}</Descriptions.Item>
        <Descriptions.Item label="Description">{shortUrls.publicDescription}</Descriptions.Item>
        <Descriptions.Item label="Expire Date">{shortUrls.publicExpireDate}</Descriptions.Item>
        <Descriptions.Item label="Active">{shortUrls.publicIsActive ? "O" :"X"}</Descriptions.Item>
    </Descriptions>}

          { isToggled && hasPrivate && <Descriptions title="Private URL" bordered>
        <Descriptions.Item label="Short URL">{shortUrls.privateShortUrl}</Descriptions.Item>
        <Descriptions.Item label="Description">{shortUrls.privateDescription}</Descriptions.Item>
        <Descriptions.Item label="Expire Date">{shortUrls.privateExpireDate}</Descriptions.Item>
        <Descriptions.Item label="Active">{shortUrls.privateIsActive ? "O" : "X"}</Descriptions.Item>
    </Descriptions>}
          </>
      );
}