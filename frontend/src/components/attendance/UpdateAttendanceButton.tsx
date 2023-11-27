import { Attendance } from "../../entities/attendance/Attendance.ts";
import { useDisclosure } from "@chakra-ui/react";
import EditButton from "../shared/EditButton.tsx";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import AttendanceForm from "./AttendanceForm.tsx";
import { FormType } from "../../entities/FormType.ts";
import { useParams } from "react-router-dom";
import useUpdateAttendanceForm from "../../hooks/attendance/useUpdateAttendanceForm.ts";

interface Props {
  attendance: Attendance;
}

const UpdateAttendanceButton = ({ attendance }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const updateAttendanceForm = useUpdateAttendanceForm(
    courseId!,
    attendance,
    onClose,
  );

  return (
    <>
      <EditButton onClick={onOpen} />
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Update Attendance"}
      >
        <AttendanceForm
          formType={FormType.UPDATE}
          form={updateAttendanceForm}
          submitButtonText={"Edit"}
        />
      </CustomDrawer>
    </>
  );
};
export default UpdateAttendanceButton;
