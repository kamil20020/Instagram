import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { CommentData } from "../../models/responses/CommentData";
import { Page } from "../../models/responses/Page";
import { commentSelector, clearComment } from "../../redux/slices/commentSlice";
import CommentAPIService from "../../services/CommentAPIService";
import CommentView from "./CommentView";

const SubCommentsView = (props: {
    postId: string;
    comment: CommentData;
}) => {

    const comment = props.comment

    const [subComments, setSubComments] = React.useState<CommentData[]>([])
    const [showSubComments, setShowSubComments] = React.useState<boolean>(false);
    const [page, setPage] = React.useState<number>(0)
    const [totalPages, setTotalPages] = React.useState<number>(0);
    const pageSize = 12;

    const commentState = useSelector(commentSelector)

    const dispatch = useDispatch()

    const getCommentsPage = (newPage: number) => {

        CommentAPIService.getCommentsPage(props.postId, {page: newPage, size: pageSize}, comment.id)
        .then((response) => {
          const pagedRespone: Page = response.data
          const newSubComments: CommentData[] = pagedRespone.content
    
          setSubComments([...subComments, ...newSubComments])
          setPage(newPage)
          setTotalPages(pagedRespone.totalPages)
          setShowSubComments(true)
        })
    }

    const getCommentsPageWithoutAppend = (newPage: number) => {

        CommentAPIService.getCommentsPage(props.postId, {page: newPage, size: pageSize}, comment.id)
        .then((response) => {
          const pagedRespone: Page = response.data
          const newSubComments: CommentData[] = pagedRespone.content
    
          setSubComments(newSubComments)
          setPage(newPage)
          setTotalPages(pagedRespone.totalPages)
          setShowSubComments(true)
        })
    }

    const handleSwitchShowComments = () => {
        
        if(showSubComments){
            setShowSubComments(false)
            return
        }

        getCommentsPageWithoutAppend(0)
    }
    
    const onDelete = (id: string) => {
        setSubComments(
            subComments.filter((subComment: CommentData) => subComment.id !== id)
        )
    }

    if(commentState.parentCommentId === comment.id && !commentState.isCreating){
        getCommentsPageWithoutAppend(0)
        setPage(0)
        dispatch(clearComment())
    }

    return (
        <div className="sub-comments">
            <button 
                className="grey-button-outlined" 
                onClick={handleSwitchShowComments}
            >
                {!showSubComments ? `Wyświetl odpowiedzi (${2})` : "Ukryj odpowiedzi"}
            </button>
            <div className="sub-comments-content">
                {showSubComments && subComments.map((childComment: CommentData) => (
                    <CommentView 
                        key={childComment.id} 
                        postId={props.postId} 
                        comment={childComment}
                        onDelete={onDelete}
                        parentCommentId={comment.id}
                    />
                ))}
            </div>
            {page < (totalPages - 1) &&
                <div className="load-more-subcomments" style={{display: "flex", justifyContent: "center"}}>
                    <button 
                        className="grey-button"
                        onClick={() => getCommentsPage(page + 1)} 
                    >
                        Doładuj komentarze
                    </button>
                </div>
            }
        </div>
    )
}

export default SubCommentsView;