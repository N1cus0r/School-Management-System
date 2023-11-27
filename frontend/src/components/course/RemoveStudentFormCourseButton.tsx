import { Course } from "../../entities/Course.ts";
import { Button, useDisclosure } from "@chakra-ui/react";
import useCourseStudentForm from "../../hooks/course/useCourseStudentForm.ts";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import CourseStudentForm from "./CourseStudentForm.tsx";
import useRemoveStudentFromCourse from "../../hooks/course/useRemoveStudentFromCourse.ts";
import { OperationType } from "../../entities/OperationType.ts";

interface Props {
  course: Course;
}

const RemoveStudentFormCourseButton = ({ course }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const removeStudentFromCourse = useRemoveStudentFromCourse(
    course.id,
    onClose,
  );
  const removeStudentFromCourseForm = useCourseStudentForm((values) =>
    removeStudentFromCourse.mutate(values.studentId),
  );

  return (
    <>
      <Button colorScheme={"pink"} onClick={onOpen}>
        Remove Student
      </Button>
      <CustomDrawer
        isOpen={isOpen}
        onClose={onClose}
        heading={"Remove Student"}
      >
        <CourseStudentForm
          operationType={OperationType.DELETE}
          courseId={course.id}
          form={removeStudentFromCourseForm}
        />
      </CustomDrawer>
    </>
  );
};
export default RemoveStudentFormCourseButton;
