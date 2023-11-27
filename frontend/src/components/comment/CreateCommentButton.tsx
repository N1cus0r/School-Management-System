import {useDisclosure} from "@chakra-ui/react";
import {useParams} from "react-router-dom";
import CreateButton from "../shared/CreateButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import {FormType} from "../../entities/FormType.ts";
import useCreateCommentForm from "../../hooks/comment/useCreateCommentForm.ts";
import CommentForm from "./CommentForm.tsx";

const CreateCommentButton = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const createCommentForm = useCreateCommentForm(courseId!, onClose);

  return (
    <>
      <CreateButton onClick={onOpen}>Add Comment</CreateButton>
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Create an Attendance"}
      >
        <CommentForm
          formType={FormType.CREATE}
          form={createCommentForm}
          submitButtonText={"Create"}
        />
      </CustomDrawer>
    </>
  );
};
export default CreateCommentButton;
