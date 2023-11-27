import DeleteButton from "../shared/DeleteButton.tsx";
import { useDisclosure } from "@chakra-ui/react";
import useDeleteCourse from "../../hooks/course/useDeleteCourse.ts";
import { CACHE_KEY_COURSES } from "../../utils/constants.ts";
import notificationService from "../../services/notification-service.ts";
import DeleteModal from "../shared/DeleteModal.tsx";
import { Course } from "../../entities/Course.ts";

interface Props {
  course: Course;
}

const DeleteCourseButton = ({ course: { id } }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();

  const deleteCourse = useDeleteCourse(
    CACHE_KEY_COURSES,
    onClose,
    () => {
      notificationService.successNotification(
        "Success",
        "Course was successfully deleted",
      );
    },
    () => {
      notificationService.errorNotification(
        "Error",
        "An error occurred while trying to delete course",
      );
    },
  );

  return (
    <>
      <DeleteButton onClick={onOpen} />
      <DeleteModal
        isOpen={isOpen}
        onClose={onClose}
        onClick={() => deleteCourse.mutate(id)}
        heading={"Deleting Course"}
        text={"Are you sure you want to delete this course ?"}
      />
    </>
  );
};
export default DeleteCourseButton;
