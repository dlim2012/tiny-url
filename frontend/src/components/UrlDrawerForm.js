import {Drawer, Input, Col, Form, Row, Button, Switch, Spin } from 'antd';
import { postWithJwt } from '../clients'
import { LoadingOutlined } from '@ant-design/icons';
import { useState } from 'react';
import { successNotification, errorNotification } from '../Notification'

const { TextArea } = Input;

const antIcon = (
    <LoadingOutlined
      style={{
        fontSize: 24,
      }}
      spin
    />
  );

const choices = [{
    longUrl: "https://github.com/dlim2012/tiny-url-system",
    description: "GitHub repository"
    },{
    longUrl: "https://www.google.com",
    description: "A search engine for the world's information."
    },{
    longUrl: "https://www.airbnb.com",
    description: "An online marketplace for short-term homestays."
    },{
    longUrl: "https://www.reddit.com",
    description: "A website for network of communities."
    },{
    longUrl: "https://www.weather.com",
    description: "Weather Today Across the Country"
    },{
    longUrl: "https://www.yahoo.com",
    description: "Latest news coverage, email, etc"
    },{
    longUrl: "https://www.amazon.com",
    description: "An E-commerce website."
    },{
    longUrl: "https://www.walmart.com",
    description: "A multinational retail corporation."
    },{
    longUrl: "https://www.ebay.com",
    description: "A website for consumer-to-consumer and business-to-consumer sales"
    },{
    longUrl: "https://www.wikipedia.com",
    description: "Free encyclopedia"
    }
];


export function UrlDrawerForm({showDrawer, setShowDrawer, fetchUrls}) {
    const [form] = Form.useForm();
    const onClose = () => setShowDrawer(false);
    const [submitting, setSubmitting] = useState(false);

    const onFinish = values => {
        console.log(values.isPrivate)
        const isPrivate = values.isPrivate === true
        console.log(isPrivate)
        setSubmitting(true);
        const payload = {
            longUrl: values.longUrl,
            shortUrlPath : values.shortUrlPath == null ? "" : values.shortUrlPath,
            description : values.description == null ? "" : values.description,
            isPrivate : isPrivate
        }
        postWithJwt("/api/v1/user/urls/generate", payload)
        .then(() => {
                console.log("URL added");
                onClose();
                successNotification("URL added", ``)
                fetchUrls(2);
            }
            )
            .catch(err => {
                err.response.json().then(data => {
                    console.log(data)
                    errorNotification("Fetch URL Failed", `${data.message}`)
                })
            })
            .finally(() => {
                setSubmitting(false);
            })
    };

    const onFinishFailed = errorInfo => {
        console.log(JSON.stringify(errorInfo, null, 2));
    };
    
    const onFill = () => {
        form.setFieldsValue(choices[Math.floor(Math.random() * choices.length)]);
      }

    return <Drawer
        title="Create new URL"
        width={720}
        onClose={onClose}
        open={showDrawer}
        bodyStyle={{paddingBottom: 80}}
        footer={
            <div
                style={{
                    textAlign: 'right',
                }}
            >
                <Button onClick={onClose} style={{marginRight: 8}}>
                    Cancel
                </Button>
            </div>
        }
    >
        <Form layout="vertical"
              form={form}
              onFinishFailed={onFinishFailed}
              onFinish={onFinish}
              requiredMark>
            <Row gutter={32}>
                <Col span={24}>
                    <Form.Item
                        name="longUrl"
                        label="Original URL"
                        rules={[
                            {required: true, message: 'Please enter a URL'},
                            {
                                pattern: /^https:\/\//,
                                message: 'Custom path should start with \"https://\"'
                            }
                        ]}
                    >
                        <Input placeholder='Enter an Original URL (Required)'/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={32}>
                <Col span={24}>
                    <Form.Item
                        name="shortUrlPath"
                        label="Custom Path"
                        rules={[{
                            pattern: /^[a-zA-Z0-9\-\_]+$/,
                            message: 'Custom path can only include alphanumeric characters, underscore, and dash.'
                        },
                            {
                                pattern: /^[a-zA-Z0-9\-\_]{8,50}$/,
                                // pattern: /{8,}$/,
                                message: "Custom path should be either empty or at least 8 characters"
                            },
                            {
                              pattern: /^[a-zA-Z0-9\-\_]{1,50}$/,
                                message: "Custom path should have length at most 50"
                            }
                        ]}
                    >
                        <Input placeholder="Enter custom URL path (Optional)"/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={32}>
                <Col span={24}>
                    <Form.Item
                        name="description"
                        label="Note"
                    >
                    <TextArea rows={6} placeholder="Enter a note (Optional)"/>
                    </Form.Item>
                </Col>
            </Row>
            <Row gutter={32}>
                <Col span={24}>
                    <Form.Item
                        name="isPrivate"
                        valuePropName="checked"
                        label="Private URL"
                    >
                    <Switch />
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                <Col span={3}>
                    <Form.Item >
                        <Button type="primary" htmlType="submit">
                            Submit
                        </Button>
                    </Form.Item>
                </Col>
                <Col span={3}>
                    <Form.Item >
                        <Button type="link" htmlType="button" onClick={onFill}>
                        Fill form
                        </Button>
                    </Form.Item>
                </Col>
            </Row>
            <Row>
                { submitting && <Spin indicator={antIcon} />}
            </Row>
        </Form>
    </Drawer>
}
