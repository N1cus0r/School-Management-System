import {Comment} from "../../entities/Comment.ts";
import CardContainer from "../shared/CardContainer.tsx";
import {Heading, HStack, Stack, Text} from "@chakra-ui/react";
import TextModal from "../shared/TextModal.tsx";
import DeleteCommentButton from "./DeleteCommentButton.tsx";
import UpdateCommentButton from "./UpdateCommentButton.tsx";

interface Props {
  comment: Comment;
}

const CommentCard = ({ comment }: Props) => {
  return (
    <CardContainer>
      <Stack spacing={4} align={"center"}>
        <TextModal header={"Comment Description"} text={comment.text} />
        <Heading size={"md"}>{comment.studentName}</Heading>
        <Text color={"gray.500"}>Published {comment.datePublished}</Text>
      </Stack>
      <HStack>
        <UpdateCommentButton comment={comment}/>
        <DeleteCommentButton comment={comment}/>
      </HStack>
    </CardContainer>
  );
};
export default CommentCard;
