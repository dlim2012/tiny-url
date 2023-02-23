

import Cookies from "universal-cookie";
import { useState, useEffect } from "react";
import { Descriptions, Spin } from 'antd';
import { getProfile } from '../clients';
import { loadingIcon } from './utils';



export const UserProfile = () => {
    const cookies = new Cookies();
    const [profile, setProfile] = useState([]);
    const [fetching, setFetching] = useState(true);

    const fetchProfile = () =>
        getProfile(cookies.get("jwt_authentication"))
            .then(response => response.json())
            .then(data => {
                setProfile(data);
                setFetching(false);
            })

    useEffect(() => {
        console.log("User profile mounted");
        fetchProfile();
    }, []);

    if (fetching) {
        return <Spin indicator={loadingIcon} />
    }
    return (
        <Descriptions title="User Info" bordered>
          <Descriptions.Item label="First Name">{profile.firstname}</Descriptions.Item>
          <Descriptions.Item label="Last Name">{profile.lastname}</Descriptions.Item>
          <Descriptions.Item label="Email">{profile.email}</Descriptions.Item>
          <Descriptions.Item label="Available URLs">{profile.availableShortUrl}</Descriptions.Item>
          <Descriptions.Item label="Created At">{profile.createdAt}</Descriptions.Item>
        </Descriptions>
      );
}