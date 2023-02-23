import {Drawer, Input, Col, Select, Form, Row, Button, Switch, Spin } from 'antd';
import { getGenerateUrlResponse } from '../clients'
import Cookies from "universal-cookie";
import { LoadingOutlined } from '@ant-design/icons';
import { useState } from 'react';

const {Option} = Select;

const { TextArea } = Input;

const antIcon = (
    <LoadingOutlined
      style={{
        fontSize: 24,
      }}
      spin
    />
  );

export function UrlDrawerForm({showDrawer, setShowDrawer, fetchUrls}) {
    const [form] = Form.useForm();
    const cookies = new Cookies();
    const onClose = () => setShowDrawer(false);
    const [submitting, setSubmitting] = useState(false);

    const onFinish = values => {
        setSubmitting(true);
        getGenerateUrlResponse(cookies.get("jwt_authentication"),
            {
                longUrl: values.longUrl,
                shortUrlPath : values.shortUrlPath == null ? "" : values.shortUrlPath,
                description : values.description == null ? "" : values.description,
                isPrivate : values.isPrivate == null ? false : true
            }
            ).then(() => {
                console.log("URL added");
                onClose();
                console.log(fetchUrls)
                fetchUrls();
            }
            )
            .catch(error => {console.log(error)})
            .finally(() => {
                setSubmitting(false);
            })
    };

    const onFinishFailed = errorInfo => {
        alert(JSON.stringify(errorInfo, null, 2));
    };
    
    const onFill = () => {
        form.setFieldsValue({
          longUrl: "https://github.com/dlim2012/tiny-url-system",
          description: "GitHub repository"
        });
      }

    return <Drawer
        title="Create new URL"
        width={360}
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
                                pattern: /^{8, }+$/,
                                // pattern: /{8,}$/,
                                message: "Custom path should be either empty or at least 8 characters"
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
                        label="Description"
                    >
                    <TextArea rows={6} placeholder="Enter descriptions (Optional)"/>
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
                <Col span={6}>
                    <Form.Item >
                        <Button type="primary" htmlType="submit">
                            Submit
                        </Button>
                    </Form.Item>
                </Col>
                <Col span={6}>
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
