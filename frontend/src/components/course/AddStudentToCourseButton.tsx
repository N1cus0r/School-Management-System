import { Button, useDisclosure } from "@chakra-ui/react";
import { Course } from "../../entities/Course.ts";
import CustomDrawer from "../shared/CustomDrawer.tsx";
import CourseStudentForm from "./CourseStudentForm.tsx";
import useCourseStudentForm from "../../hooks/course/useCourseStudentForm.ts";
import useAddStudentToCourse from "../../hooks/course/useAddStudentToCourse.ts";
import { OperationType } from "../../entities/OperationType.ts";

interface Props {
  course: Course;
}

const AddStudentToCourseButton = ({ course }: Props) => {
  const { isOpen, onOpen, onClose } = useDisclosure();
  const addStudentToCourse = useAddStudentToCourse(course.id, onClose);
  const addStudentToCourseForm = useCourseStudentForm((values) =>
    addStudentToCourse.mutate(values.studentId),
  );

  return (
    <>
      <Button colorScheme={"purple"} onClick={onOpen}>
        Add Student
      </Button>
      <CustomDrawer isOpen={isOpen} onClose={onClose} heading={"Add Student"}>
        <CourseStudentForm
          operationType={OperationType.ADD}
          courseId={course.id}
          form={addStudentToCourseForm}
          submitButtonText={"Add"}
        />
      </CustomDrawer>
    </>
  );
};
export default AddStudentToCourseButton;
