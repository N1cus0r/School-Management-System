import DeleteButton from "../shared/DeleteButton.tsx";
import DeleteModal from "../shared/DeleteModal.tsx";
import { Grade } from "../../entities/Grade.ts";
import { useDisclosure } from "@chakra-ui/react";
import { useParams } from "react-router-dom";
import useDeleteGrade from "../../hooks/grade/useDeleteGrade.ts";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";
import notificationService from "../../services/notification-service.ts";

interface Props {
  grade: Grade;
}
const DeleteGradeButton = ({ grade: {id} }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const deleteGrade = useDeleteGrade(
    [...CACHE_KEY_COURSES, courseId, "grades"],
    onClose,
    () => {
      notificationService.successNotification(
        "Success",
        "Grade was successfully deleted",
      );
    },
    () => {
      notificationService.errorNotification(
        "Error",
        "An error occurred while trying to delete grade",
      );
    },
  );

  return (
    <>
      <DeleteButton onClick={onOpen} />
      <DeleteModal
        isOpen={isOpen}
        onClose={onClose}
        onClick={() => deleteGrade.mutate(id)}
        heading={"Deleting Grade"}
        text={"Are you sure you want to delete this grade ?"}
      />
    </>
  );
};
export default DeleteGradeButton;
