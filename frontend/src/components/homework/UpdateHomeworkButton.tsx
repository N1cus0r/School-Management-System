import {Homework} from "../../entities/Homework.ts";
import {useDisclosure} from "@chakra-ui/react";
import EditButton from "../shared/EditButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import {useParams} from "react-router-dom";
import useUpdateHomeworkForm from "../../hooks/homework/useUpdateHomeworkForm.ts";
import HomeworkForm from "./HomeworkForm.tsx";

interface Props {
  homework: Homework;
}
const UpdateHomeworkButton = ({ homework }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const updateHomeworkForm = useUpdateHomeworkForm(
    courseId!,
    homework,
    onClose,
  );

  return (
    <>
      <EditButton onClick={onOpen} />
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Update Homework"}
      >
        <HomeworkForm form={updateHomeworkForm} submitButtonText={"Edit"}/>
      </CustomDrawer>
    </>
  );
};
export default UpdateHomeworkButton;
