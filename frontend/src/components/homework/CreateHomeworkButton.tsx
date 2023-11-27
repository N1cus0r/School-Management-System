import CreateButton from "../shared/CreateButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import { useDisclosure } from "@chakra-ui/react";
import HomeworkForm from "./HomeworkForm.tsx";
import { useParams } from "react-router-dom";
import useCreateHomeworkForm from "../../hooks/homework/useCreateHomeworkForm.ts";

const CreateHomeworkButton = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const createHomeworkForm = useCreateHomeworkForm(courseId!, onClose);

  return (
    <>
      <CreateButton onClick={onOpen}>Add Homework</CreateButton>
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Create a Homework"}
      >
        <HomeworkForm form={createHomeworkForm} submitButtonText={"Create"}/>
      </CustomDrawer>
    </>
  );
};
export default CreateHomeworkButton;
