import { Comment } from "../../entities/Comment.ts";
import DeleteButton from "../shared/DeleteButton.tsx";
import DeleteModal from "../shared/DeleteModal.tsx";
import { useDisclosure } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";
import notificationService from "../../services/notification-service.ts";
import useDeleteComment from "../../hooks/comment/useDeleteComment.ts";

interface Props {
  comment: Comment;
}

const DeleteCommentButton = ({ comment: { id } }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const deleteComment = useDeleteComment(
    [...CACHE_KEY_COURSES, courseId, "comments"],
    onClose,
    () => {
      notificationService.successNotification(
        "Success",
        "Comment was successfully deleted",
      );
    },
    () => {
      notificationService.errorNotification(
        "Error",
        "An error occurred while trying to delete comment",
      );
    },
  );

  return (
    <>
      <DeleteButton onClick={onOpen} />
      <DeleteModal
        isOpen={isOpen}
        onClose={onClose}
        onClick={() => deleteComment.mutate(id)}
        heading={"Deleting Comment"}
        text={"Are you sure you want to delete this comment ?"}
      />
    </>
  );
};
export default DeleteCommentButton;
