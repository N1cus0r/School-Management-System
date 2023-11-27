import CreateButton from "../shared/CreateButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import { useDisclosure } from "@chakra-ui/react";
import CreateCourseForm from "./CreateCourseForm.tsx";

const CreateCourseButton = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();

  return (
    <>
      <CreateButton onClick={onOpen}>Add Course</CreateButton>
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Create a Course"}
      >
        <CreateCourseForm onSuccess={onClose} />
      </CustomDrawer>
    </>
  );
};
export default CreateCourseButton;
