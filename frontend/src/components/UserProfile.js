

import { useState, useEffect } from "react";
import { Descriptions, Spin, Button } from 'antd';
import { getProfile } from '../clients';
import { loadingIcon } from './utils';
import { postWithJwt } from '../clients'
import { successNotification, errorNotification } from '../Notification'



export const UserProfile = () => {
    const [profile, setProfile] = useState([]);
    const [fetching, setFetching] = useState(true);
    const refillPayload = {number : 1000}

    const fetchProfile = () =>
        getProfile()
            .then(response => response.json())
            .then(data => {
                setProfile(data);
                setFetching(false);
            }).catch(error => 
              error.response.json().then(data => {
                console.log(data)
                errorNotification("Fetching profile failed", `${data.message}`)
              }))

    const refillRequest = () =>
            postWithJwt("/api/v1/user/refill", refillPayload)
            .then(
              successNotification("Available URLs added", `${profile.availableShortUrl} -> ${profile.availableShortUrl + refillPayload.number}`))
            .catch(error => 
              error.response.json().then(data => {
                console.log(data)
                errorNotification("Add to # available URLs Failed", `${data.message}`)
              }))
            .then(() => fetchProfile())

    useEffect(() => {
        console.log("User profile mounted");
        fetchProfile();
    }, []);

    if (fetching) {
        return <Spin indicator={loadingIcon} />
    }
    return (
        <>
        <Descriptions 
        title="User Info" bordered>
          <Descriptions.Item label="First Name">{profile.firstname}</Descriptions.Item>
          <Descriptions.Item label="Last Name">{profile.lastname}</Descriptions.Item>
          <Descriptions.Item label="Email">{profile.email}</Descriptions.Item>
          <Descriptions.Item label="# Unexpired URLs">{profile.numUrl}</Descriptions.Item>
          <Descriptions.Item label="# URL balance"><>{profile.availableShortUrl}&nbsp;&nbsp;
            <Button size="small" onClick={() => refillRequest()}>Add</Button></></Descriptions.Item>
          <Descriptions.Item label="Created At">{profile.createdAt}</Descriptions.Item>
        </Descriptions>
        
        </>
      );
}
