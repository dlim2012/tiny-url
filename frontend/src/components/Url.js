import fetch from 'unfetch'
import Cookies from "universal-cookie";
import { useState, useEffect } from "react";
import { Table, Spin, Empty, Button } from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { UrlDrawerForm } from './UrlDrawerForm'
import { getUrl } from '../clients'
import { loadingIcon } from './utils';


const columns = [
    {
        title: 'Index',
        dataIndex: 'id',
        key: 'id',
        sorter: (a, b) => a.id - b.id,
        render: (id, record, index) => { ++index; return index; },
        showSorterTooltip: false,
        width: "10%",
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
        title: 'Description',
        dataIndex: 'description',
        key: 'description',
        width: "25%",
    },
    {
        title: 'Private',
        dataIndex: 'isPrivate',
        key: 'isPrivate',
        width: "7.5%",
    },
    {
        title: 'Active',
        dataIndex: 'isActive',
        key: 'isActive',
        width: "7.5%",
    },
    {
        title: 'Expire Date',
        dataIndex: 'expiresAt',
        key: 'expiresAt',
        width: "10%"
    }
  ];



export function Url() {
    const cookies = new Cookies();
    const navigate = useNavigate();
    const [urls, setUrls] = useState([]);
    const [fetching, setFetching] = useState(true);
    const [showDrawer, setShowDrawer] = useState(false);

    const fetchUrls = () => 
        getUrl(cookies.get("jwt_authentication"))
            .then(response => response.json())
            .then(data => {
                setUrls(data);
                setFetching(false);
            })
        

    useEffect(() => {
        fetchUrls();
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
        title={()=><Button 
            type="primary" 
            shape="round" 
            icon={<PlusOutlined /> } 
            size="small" 
            onClick={() => setShowDrawer(!showDrawer)}
            >Add New URL</Button>}
        pagination={{pageSize: 50}}
        scroll={{y: 1000}}
        rowKey="Id"
        />
        </>;
    
}

