import { useDisclosure } from "@chakra-ui/react";
import DeleteButton from "../shared/DeleteButton.tsx";
import DeleteModal from "../shared/DeleteModal.tsx";
import { Homework } from "../../entities/Homework.ts";
import useDeleteHomework from "../../hooks/homework/useDeleteHomework.ts";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";
import notificationService from "../../services/notification-service.ts";
import { useParams } from "react-router-dom";

interface Props {
  homework: Homework;
}

const DeleteHomeworkButton = ({ homework: { id } }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const { courseId } = useParams();
  const deleteHomework = useDeleteHomework(
    [...CACHE_KEY_COURSES, courseId, "homeworks"],
    onClose,
    () => {
      notificationService.successNotification(
        "Success",
        "Homework was successfully deleted",
      );
    },
    () => {
      notificationService.errorNotification(
        "Error",
        "An error occurred while trying to delete homework",
      );
    },
  );

  return (
    <>
      <DeleteButton onClick={onOpen} />
      <DeleteModal
        isOpen={isOpen}
        onClose={onClose}
        onClick={() => deleteHomework.mutate(id)}
        heading={"Deleting Homework"}
        text={"Are you sure you want to delete this homework ?"}
      />
    </>
  );
};
export default DeleteHomeworkButton;
