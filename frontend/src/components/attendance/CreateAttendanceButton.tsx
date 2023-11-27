import CreateButton from "../shared/CreateButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import { useDisclosure } from "@chakra-ui/react";
import AttendanceForm from "./AttendanceForm.tsx";
import { useParams } from "react-router-dom";
import useCreateAttendanceForm from "../../hooks/attendance/useCreateAttendanceForm.ts";
import { FormType } from "../../entities/FormType.ts";

const CreateAttendanceButton = () => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const createAttendanceForm = useCreateAttendanceForm(courseId!, onClose);

  return (
    <>
      <CreateButton onClick={onOpen}>Add Attendance</CreateButton>
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Create an Attendance"}
      >
        <AttendanceForm
          formType={FormType.CREATE}
          form={createAttendanceForm}
          submitButtonText={"Create"}
        />
      </CustomDrawer>
    </>
  );
};
export default CreateAttendanceButton;
