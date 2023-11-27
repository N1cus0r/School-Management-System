import {Comment} from "../../entities/Comment.ts";
import CardsContainer from "../shared/CardsContainer.tsx";
import React from "react";
import CommentCard from "./CommentCard.tsx";

interface Props {
    comments: Comment[]
}

const CommentsCards = ({comments}: Props) => {
    return (
        <CardsContainer>
            {comments.map((comment) => (
                <React.Fragment key={comment.id}>
                    <CommentCard comment={comment}/>
                </React.Fragment>
            ))}
        </CardsContainer>
    )
}
export default CommentsCards
