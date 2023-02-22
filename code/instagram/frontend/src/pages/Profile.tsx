import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { Profile } from "../models/Profile";
import { NotificationType, setNotification } from "../redux/slices/notificationSlice";
import { setLatestProfiles, userPreferencesSelector } from "../redux/slices/userPreferencesSlice";
import UserAPIService from "../services/UserAPIService";

const ProfileView = () => {
    
    let profileUserId: number = Number(useParams().id)

    const [userProfile, setUserProfile] = React.useState<Profile>()

    const latestProfilesIds = useSelector(userPreferencesSelector).latestProfilesIds

    const dispatch = useDispatch()

    useEffect(() => {
        if(!Number.isInteger(profileUserId)){
            return;
        }
        
        UserAPIService.getUserById(profileUserId)
        .then((response) => {
            setUserProfile(response.data)
        })
        .catch((error) => {
            dispatch(setNotification({type: NotificationType.success, message: error.message}))
        })
    }, [profileUserId])

    if(!Number.isInteger(profileUserId)){
        return <h1>Podano niewłaściwe id użytkownika</h1>
    }

    return (
        <div>
            {userProfile?.nickname}
        </div>
    )
}

export default ProfileView;