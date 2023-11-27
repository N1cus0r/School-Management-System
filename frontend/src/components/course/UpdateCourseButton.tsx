import {useDisclosure} from "@chakra-ui/react";
import {Course} from "../../entities/Course.ts";
import EditButton from "../shared/EditButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import UpdateCourseForm from "./UpdateCourseForm.tsx";

interface Props {
  course: Course;
}

const UpdateCourseButton = ({ course }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  return (
    <>
      <EditButton onClick={onOpen} />
      <CustomDrawer isOpen={isOpen} onClose={onClose} heading={"Update Course"}>
        <UpdateCourseForm course={course} onSuccess={onClose}/>
      </CustomDrawer>
    </>
  );
};
export default UpdateCourseButton;
