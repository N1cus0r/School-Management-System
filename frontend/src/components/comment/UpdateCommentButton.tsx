import { Comment } from "../../entities/Comment.ts";
import EditButton from "../shared/EditButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import { FormType } from "../../entities/FormType.ts";
import { useDisclosure } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import useUpdateCommentForm from "../../hooks/comment/useUpdateCommentForm.ts";
import CommentForm from "./CommentForm.tsx";

interface Props {
  comment: Comment;
}

const UpdateCommentButton = ({ comment }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const updateCommentForm = useUpdateCommentForm(courseId!, comment, onClose);
  return (
    <>
      <EditButton onClick={onOpen} />
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Update Comment"}
      >
        <CommentForm
          formType={FormType.UPDATE}
          form={updateCommentForm}
          submitButtonText={"Edit"}
        />
      </CustomDrawer>
    </>
  );
};
export default UpdateCommentButton;
