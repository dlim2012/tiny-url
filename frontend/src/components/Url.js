
import { useState, useEffect } from "react";
import { Table, Spin, Empty, Button, Switch, Space, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { UrlDrawerForm } from './UrlDrawerForm'
import { putWithJwt, postWithJwt } from '../clients'
import { loadingIcon } from './utils';
import { successNotification, errorNotification } from '../Notification'

export function Url() {
    const navigate = useNavigate();
    const [urls, setUrls] = useState([]);
    const [fetching, setFetching] = useState(true);
    const [showDrawer, setShowDrawer] = useState(false);


// Issue: antd sort function does not work properly (only one works among ascending and descending)
// Issue: default switch in table does not reflect the corresponding rows when filtered/sorted


const columns = [
        {
            title: 'Index',
            dataIndex: 'index',
            key: 'index',
            // sorter: (a, b) => a.index - b.index,
            // showSorterTooltip: false,
            width: "5%",
        },
        {
        title: 'Original URL',
        dataIndex: 'longUrl',
        key: 'longUrl',
        width: "25%",
        },
        {
        title: 'Short URL',
        dataIndex: 'shortUrl',
        key: 'shortUrl',
        width: "15%",
        },
        {
            title: 'Note',
            dataIndex: 'description',
            key: 'description',
            width: "20%",
        },
        {
            title: 'Private',
            dataIndex: 'isPrivate',
            key: 'isPrivate',
            width: "5%",
            // filters: [
            //     {
            //         text: "Private",
            //         value: "O"
            //     },
            //     {
            //         text: "Public",
            //         value: "X"
            //     }
            // ],
            // onFilter: (value, record) => record.isPrivate === value
            render: (text, record) => { return text}
        },
        {
            title: 'Active',
            key: 'isActive',
            dataIndex: 'isActive',
            width: "5%",
            // filters: [
            //     {
            //         text: "Active",
            //         value: "O"
            //     },
            //     {
            //         text: "Inactive",
            //         value: "X"
            //     }
            // ],
            // onFilter: (value, record) => {return record.isActive === value;},
            render: (text, record) => 
            { return <Switch 
                onClick={
                    (click) => {
                        const payload = {
                            'shortUrl': record.shortUrl,
                            'isPrivate': record.isPrivate === "O" ? true : false,
                            'isActive': click
                        };
                        putWithJwt("/api/v1/user/urls/set-is-active", payload)
                            .then(response => {
                                if (click){
                                    successNotification("URL activated")
                                } else {
                                    successNotification("URL disactivated")
                                }
                            }).catch(error => {
                                error.response.json().then(data => {
                                  console.log(data)
                                  if (click){
                                    errorNotification("Activate URL failed")
                                  } else {
                                    errorNotification("Disactivate URL failed")
                                  }
                                })});
                        fetchUrls(2)
                    }
                }
                defaultChecked={record.isActive==="O"}></Switch>
        }
        },
        {
            title: 'Expire Date',
            dataIndex: 'expiresAt',
            key: 'expiresAt',
            // sorter:(a, b) => a.expiresAt > b.expiresAt,
            showSorterTooltip: false,
            width: "7.5%"
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, record) => (
              <Space size="middle">
                <Button  
            shape="round" 
            // icon={<PlusOutlined /> } 
            size="small" 
            onClick={() => {
                localStorage.setItem("longUrl", record.longUrl);
                localStorage.setItem("isPrivate", record.isPrivate)
                navigate('/pages/user/url-edit-path')
            }}
            >Edit Path</Button>
            <Button  
            shape="round" 
            // icon={<PlusOutlined /> } 
            size="small" 
            onClick={() => {
                localStorage.setItem("longUrl", record.longUrl);
                localStorage.setItem("isPrivate", record.isPrivate)
                navigate('/pages/user/url-extend')
            }}
            >Extend</Button>
            <div>
            <Popconfirm
                placement="topRight"
                title={'Are you sure to delete this URL?'}
                description={'Delete the URL'}
                onConfirm={() =>{
                    const payload = {
                        shortUrlToDelete: record.shortUrl,
                        isActiveForGetUrls: 2
                    };
                    console.log(payload)
                    postWithJwt("/api/v1/user/urls/delete", payload)
                    .then(response => response.json())
                    .then(data => {
                        console.log(data)
                        setUrls(data);
                        setFetching(false);
                    }).catch(error => {
                        error.response.json().then(data => {
                            console.log(data)
                            errorNotification("Fetch URLs failed", `${data.message}`)
                        })
                    }).finally(() => setFetching(false))
                }}
                okText="Yes"
                cancelText="No"
            >
            <Button  
                    shape="round"  
                    size="small" 
                    onClick={() => {
                }}>
            Delete</Button></Popconfirm>
      </div>
            
              </Space>
            ),
          },
    ];

    const fetchUrls = (isActive) => 
        postWithJwt("/api/v1/user/urls", { isActive: isActive })
            .then(response => response.json())
            .then(data => {
                console.log(data)
                setUrls(data);
                setFetching(false);
            }).catch(error => {
                error.response.json().then(data => {
                    console.log(data)
                    errorNotification("Fetch URLs failed", `${data.message}`)
                })
            }).finally(() => setFetching(false))
        

    useEffect(() => {
        fetchUrls(2);
        console.log("URLs mounted");
    }, []);

    if (fetching) {
        return <Spin indicator={loadingIcon} />
    }
    if (urls.length <= 0) {
        return <>
        <UrlDrawerForm showDrawer={showDrawer} setShowDrawer={setShowDrawer} fetchUrls={fetchUrls}/>
        <br /> &nbsp;&nbsp;&nbsp;&nbsp; <Button 
        type="primary" 
        shape="round" 
        icon={<PlusOutlined /> } 
        size="small" 
        onClick={() => setShowDrawer(!showDrawer)}
        >Add New URL</Button>
        <Empty />
        </>;
    }

    function refreshPage() {
        navigate(0);
      }
    
    return <>
    <UrlDrawerForm showDrawer={showDrawer} setShowDrawer={setShowDrawer} fetchUrls={fetchUrls}/>
    <Table 
        dataSource={urls}
        columns={columns} 
        ordered 
        title={()=><><Button 
            type="primary" 
            shape="round" 
            icon={<PlusOutlined /> } 
            size="small" 
            onClick={() => setShowDrawer(!showDrawer)}
            >Add New URL</Button>
            </>}
        pagination={{pageSize: 10}}
        scroll={{y: 1000}}
        rowKey="Id"
        />
        </>;
    
}

