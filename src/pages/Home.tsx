import { useAuth0 } from "@auth0/auth0-react";
import { useEffect } from "react";
import { PostHeader } from "../models/responses/PostHeader";
import React from "react";
import FollowerAPIService from "../services/FollowerAPIService";
import { Pagination } from "../models/requests/Pagination";
import { Page } from "../models/responses/Page";
import PostView from "../features/post/post-view/PostView";
import PostImage from "../components/PostImage";
import SimpleProfileHeader from "../components/SimpleProfileHeader";
import CreateCommentView from "../features/comment/CreateCommentView";
import PostActions from "../features/post/post-view/PostActions";
import PostComments from "../features/post/post-view/PostComments";
import PostLikes from "../features/post/post-view/PostLikes";
import { Post } from "../models/responses/PostDetails";
import PostDescription from "../features/post/post-view/PostDescription";
import { Link } from "react-router-dom";
import { unstable_batchedUpdates } from "react-dom";

const Home = () => {

    const {isAuthenticated} = useAuth0();

    const [followedUsersPosts, setFollowedUsersPosts] = React.useState<Post[]>([])

    const [page, setPage] = React.useState<number>(0)
    const [pagesCount, setPagesCount] = React.useState<number>(0)
    const pageSize = 12;

    const handleLoadFollowedUsersPostsPage = (newPage: number, doReplace: boolean = false) => {

        const pagination: Pagination = {
            page: newPage,
            size: pageSize
        }

        FollowerAPIService.getPostsPage(pagination)
        .then((response) => {
            const pagedResponse: Page = response.data

            const newPosts: Post[] = pagedResponse.content

            unstable_batchedUpdates(() => {

                if(doReplace){
                    setFollowedUsersPosts(newPosts)
                }
                else{
                    setFollowedUsersPosts([...followedUsersPosts, ...newPosts])
                }

                setPage(newPage)
                setPagesCount(pagedResponse.totalPages)
            })
        })
    }

    useEffect(() => {

        if(!isAuthenticated){
            return;
        }

        handleLoadFollowedUsersPostsPage(0, true)
    }, [])

    if(!isAuthenticated){
        return <></>
    }

    return (
        <div style={{display: "flex", flexDirection: "column", rowGap: 100, marginTop: 40}}>
            {followedUsersPosts.map((post => (
                <div className="post-view">
                    <PostImage img={`data:image/png;base64,${post.content}`} />
                    <div className="post-details">
                        <div className="post-author-info">
                            <SimpleProfileHeader
                                avatar={post.author.avatar}
                                nickname={post.author.nickname}
                                userId={post.author.id}
                            />
                        </div>
                        <div className="post-comments">
                            <PostDescription post={post}/>
                            <Link to={`/post/${post.id}`}>
                                <h3 className="normal-weight opacity-hover">
                                    Zobacz wszystkie komentarze: {post.commentsCount}
                                </h3>
                            </Link>
                        </div>
                        <PostLikes 
                            post={post} 
                            postLikes={post.likesCount} 
                            isLikedPost={post.didLoggedUserLikePost}
                        />
                    </div>
                </div>
            )))}
            {page < (pagesCount - 1) &&
                <div className="load-more-followed-users-posts" style={{display: "flex", justifyContent: "center"}}>
                    <button 
                        className="grey-button"
                        style={{marginTop: 22, marginBottom: 20}}
                        onClick={() => handleLoadFollowedUsersPostsPage(page + 1)} 
                    >
                        Doładuj posty
                    </button>
                </div>
            }
        </div>
    )
}

export default Home;