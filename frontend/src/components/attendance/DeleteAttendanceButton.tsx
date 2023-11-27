import {Attendance} from "../../entities/attendance/Attendance.ts";
import DeleteButton from "../shared/DeleteButton.tsx";
import DeleteModal from "../shared/DeleteModal.tsx";
import {useDisclosure} from "@chakra-ui/react";
import {useParams} from "react-router-dom";
import {CACHE_KEY_COURSES} from "../../utils/constants.ts";
import notificationService from "../../services/notification-service.ts";
import useDeleteAttendance from "../../hooks/attendance/useDeleteAttendance.ts";

interface Props {
  attendance: Attendance;
}
const DeleteAttendanceButton = ({attendance: {id}}:Props) => {
    const { isOpen, onOpen, onClose } = useDisclosure();
    const { courseId } = useParams();
    const deleteAttendance = useDeleteAttendance(
        [...CACHE_KEY_COURSES, courseId, "attendances"],
        onClose,
        () => {
            notificationService.successNotification(
                "Success",
                "Attendance was successfully deleted",
            );
        },
        () => {
            notificationService.errorNotification(
                "Error",
                "An error occurred while trying to delete attendance",
            );
        },
    );

    return <>
      <DeleteButton onClick={onOpen} />
      <DeleteModal
          isOpen={isOpen}
          onClose={onClose}
          onClick={() => deleteAttendance.mutate(id)}
          heading={"Deleting Attendance"}
          text={"Are you sure you want to delete this attendance ?"}
      />
  </>;
};
export default DeleteAttendanceButton;
