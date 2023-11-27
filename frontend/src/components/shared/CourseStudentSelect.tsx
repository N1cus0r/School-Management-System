import { ChangeEvent } from "react";
import useCourseStudents from "../../hooks/user/useCourseStudents.ts";
import { useParams } from "react-router-dom";
import CourseEntityFormPlaceholder from "../course/CourseEntityFormPlaceholder.tsx";
import { Select } from "@chakra-ui/react";

interface Props {
  value?: number;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
}
const CourseStudentSelect = ({ value, onChange }: Props) => {
  const { courseId } = useParams();
  const { data: students } = useCourseStudents(courseId!);

  if (!students || students.pages[0].length === 0)
    return <CourseEntityFormPlaceholder />;

  return (
    <Select
      placeholder="Choose a Student"
      name="studentId"
      value={value}
      onChange={onChange}
    >
      {students.pages.map((page) =>
        page.map((student) => (
          <option key={student.id} value={student.id}>
            {student.fullName}
          </option>
        )),
      )}
    </Select>
  );
};
export default CourseStudentSelect;
