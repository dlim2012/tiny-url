
import {
    Button,
    Form,
    Input,
    Empty,
    Descriptions,
    Spin
  } from 'antd';

import { useState } from 'react';
import Cookies from "universal-cookie";
import  { getShortUrls } from '../clients'
import { loadingIcon } from './utils';


export const FindShortUrl = () => {
    
    const [shortUrls, setShortUrls] = useState({});
    const [isToggled, setIsToggled] = useState(false);
    const [hasPublic, setHasPublic] = useState(false);
    const [hasPrivate, setHasPrivate] = useState(false);
    const [fetching, setFetching] = useState(false);
    const cookies = new Cookies();

    const onFinish = (values) => {
        setFetching(true);
        try{
          getShortUrls(cookies.get("jwt_authentication"), values.longUrl)
          .then(response => response.json())
          .then(data =>{
            console.log(data)
              setShortUrls(data);
              setHasPublic(data.publicShortUrl !== '');
              setHasPrivate(data.privateShortUrl !== '');
              setIsToggled(true);
          })
        } catch(error){
            console.log(error);
        } finally {
          setFetching(false);
        }
      };


      if (fetching) {
        return <Spin indicator={loadingIcon} />
    }
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

            { isToggled && hasPublic && 
            <Descriptions title="My URLs" bordered>
                <Descriptions.Item label="Public Short URL">{shortUrls.publicShortUrl}</Descriptions.Item>
                <Descriptions.Item label="Public Description">{shortUrls.publicDescription}</Descriptions.Item>
            </Descriptions>
            }
            { isToggled && !hasPublic && hasPrivate &&
            <Descriptions title="My URLs" bordered>
                <Descriptions.Item label="Private Short URL">{shortUrls.privateShortUrl}</Descriptions.Item>
                <Descriptions.Item label="Private Description">{shortUrls.privateDescription}</Descriptions.Item>
            </Descriptions>
            }
            { isToggled && hasPublic && hasPrivate &&
            <Descriptions bordered>
                <Descriptions.Item label="Private Short URL">{shortUrls.privateShortUrl}</Descriptions.Item>
                <Descriptions.Item label="Private Description">{shortUrls.privateDescription}</Descriptions.Item>
            </Descriptions>
            }

          </>
      );
}